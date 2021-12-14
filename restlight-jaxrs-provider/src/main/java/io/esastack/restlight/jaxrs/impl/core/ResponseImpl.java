/*
 * Copyright 2021 OPPO ESA Stack Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.esastack.restlight.jaxrs.impl.core;

import esa.commons.Checks;
import esa.commons.DateUtils;
import io.esastack.commons.net.http.HttpHeaderNames;
import io.esastack.commons.net.http.HttpStatus;
import io.esastack.commons.net.http.MediaTypeUtil;
import io.esastack.restlight.core.util.HttpHeaderUtils;
import io.esastack.restlight.jaxrs.util.MediaTypeUtils;
import io.esastack.restlight.server.util.LoggerUtils;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class ResponseImpl extends Response {

    private static final IllegalStateException UNSUPPORTED_READ = new IllegalStateException("The read" +
            " operation is unsupported in server side!");

    private static final AtomicIntegerFieldUpdater<ResponseImpl> CLOSED_STATE = AtomicIntegerFieldUpdater
            .newUpdater(ResponseImpl.class, "closed");

    private final ResponseBuilderImpl builder;

    private volatile int closed = 0;
    private LinkValues linkValues;

    public ResponseImpl(ResponseBuilderImpl builder) {
        Checks.checkNotNull(builder, "builder");
        this.builder = builder;
    }

    public void setEntity(Object entity) {
        checkClosed();
        builder.entity(entity);
    }

    public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
        checkClosed();
        builder.entity(entity, annotations);
        builder.headers().putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType);
    }

    public Annotation[] getEntityAnnotations() {
        return builder.annotations();
    }

    public String getReasonPhrase() {
        return builder.reasonPhrase();
    }

    public Annotation[] getAnnotations() {
        return builder.annotations();
    }

    public void setStatus(int status) {
        builder.status(status);
    }

    public void setStatus(Response.StatusType statusInfo) {
        builder.status(statusInfo);
    }

    @Override
    public int getStatus() {
        return builder.status().getStatusCode();
    }

    @Override
    public StatusType getStatusInfo() {
        StatusType type = Status.fromStatusCode(getStatus());
        return type != null ? type : new StatusTypeImpl(getStatus());
    }

    @Override
    public Object getEntity() {
        checkClosed();
        return builder.entity();
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {
        throw UNSUPPORTED_READ;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {
        throw UNSUPPORTED_READ;
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        throw UNSUPPORTED_READ;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        throw UNSUPPORTED_READ;
    }

    @Override
    public boolean hasEntity() {
        checkClosed();
        return builder.entity() != null;
    }

    @Override
    public boolean bufferEntity() {
        return false;
    }

    @Override
    public void close() {
        CLOSED_STATE.compareAndSet(this, 0, 1);
    }

    @Override
    public MediaType getMediaType() {
        Object mediaType = builder.headers().getFirst(HttpHeaders.CONTENT_TYPE);
        if (mediaType == null || mediaType instanceof MediaType) {
            return (MediaType) mediaType;
        } else {
            return MediaTypeUtils.convert(MediaTypeUtil.parseMediaType(mediaType.toString()));
        }
    }

    @Override
    public Locale getLanguage() {
        Object language = builder.headers().getFirst(HttpHeaders.CONTENT_LANGUAGE);
        if (language == null || language instanceof Locale) {
            return (Locale) language;
        } else {
            return HttpHeaderUtils.parseToLanguage(language.toString());
        }
    }

    @Override
    public int getLength() {
        Object contentLength = builder.headers().getFirst(HttpHeaders.CONTENT_LENGTH);
        if (contentLength == null) {
            return -1;
        }

        int length;
        try {
            length = Integer.parseInt(contentLength.toString());
        } catch (NumberFormatException ignore) {
            length = -1;
        }

        return length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getAllowedMethods() {
        Object methods = builder.headers().getFirst(HttpHeaders.ALLOW);
        if (methods == null) {
            return Collections.emptySet();
        } else {
            return (Set<String>) methods;
        }
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        List<Object> cookieValues = builder.headers().get(HttpHeaders.SET_COOKIE);
        if (cookieValues == null || cookieValues.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<String, NewCookie> cookies = new HashMap<>();
            for (Object item : cookieValues) {
                NewCookie newCookie = NewCookie.valueOf(item.toString());
                cookies.put(newCookie.getName(), newCookie);
            }

            return cookies;
        }
    }

    @Override
    public EntityTag getEntityTag() {
        Object tag = builder.headers().getFirst(HttpHeaders.ETAG);
        if (tag == null || tag instanceof EntityTag) {
            return (EntityTag) tag;
        } else {
            return EntityTag.valueOf(tag.toString());
        }
    }

    @Override
    public Date getDate() {
        Object data = builder.headers().getFirst(HttpHeaders.DATE);
        if (data == null || data instanceof Date) {
            return (Date) data;
        }
        return DateUtils.toDate(data.toString(), DateUtils.yyyyMMddHHmmss);
    }

    @Override
    public Date getLastModified() {
        Object data = builder.headers().getFirst(HttpHeaders.LAST_MODIFIED);
        if (data == null || data instanceof Date) {
            return (Date) data;
        } else {
            return DateUtils.toDate(data.toString(), DateUtils.yyyyMMddHHmmss);
        }
    }

    @Override
    public URI getLocation() {
        Object location = builder.headers().getFirst(HttpHeaders.LOCATION);
        if (location == null || location instanceof URI) {
            return (URI) location;
        } else {
            return URI.create(location.toString());
        }
    }

    @Override
    public Set<Link> getLinks() {
        return getLinkValues().links;
    }

    @Override
    public boolean hasLink(String relation) {
        return getLink(relation) != null;
    }

    @Override
    public Link getLink(String relation) {
        return getLinkValues().relToLinks.get(relation);
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {
        Link link = getLink(relation);
        return link == null ? null : Link.fromLink(link);
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return builder.headers();
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return new DelegatingMultivaluedMap(builder.headers());
    }

    @Override
    public String getHeaderString(String name) {
        return HttpHeaderUtils.concatHeaderValues(getStringHeaders().get(name));
    }

    private void checkClosed() {
        if (CLOSED_STATE.get(this) == 1) {
            throw new IllegalStateException("Response has been closed!");
        }
    }

    private LinkValues getLinkValues() {
        if (this.linkValues != null) {
            return this.linkValues;
        }
        final Set<Link> links = new LinkedHashSet<>();
        List<Object> ls = builder.headers().get(HttpHeaders.LINK);
        for (Object obj : ls) {
            if (obj instanceof Link) {
                links.add((Link) obj);
            } else if (obj instanceof String) {
                for (String link : ((String) obj).split(",")) {
                    links.add(Link.valueOf(link));
                }
            } else {
                LoggerUtils.logger().warn("Unrecognized header value of 'link': [{}]", obj);
            }
        }
        final Map<String, Link> relToLinks = new LinkedHashMap<>(links.size());
        for (Link link : links) {
            for (String rel : link.getRels()) {
                relToLinks.put(rel, link);
            }
        }
        this.linkValues = new LinkValues(links, relToLinks);
        return this.linkValues;
    }

    private static class StatusTypeImpl implements StatusType {

        private final int status;

        private StatusTypeImpl(int status) {
            this.status = status;
        }

        @Override
        public int getStatusCode() {
            return status;
        }

        @Override
        public Status.Family getFamily() {
            return Status.Family.familyOf(status);
        }

        @Override
        public String getReasonPhrase() {
            return HttpStatus.valueOf(status).reasonPhrase();
        }
    }

    private static class LinkValues {

        private final Set<Link> links;
        private final Map<String, Link> relToLinks;

        private LinkValues(Set<Link> links, Map<String, Link> relToLinks) {
            this.links = Collections.unmodifiableSet(links);
            this.relToLinks = Collections.unmodifiableMap(relToLinks);
        }
    }

}