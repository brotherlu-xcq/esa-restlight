/*
 * Copyright 2022 OPPO ESA Stack Project
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
package io.esastack.restlight.core.context;

import esa.commons.Checks;
import io.esastack.commons.net.http.MediaType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * This {@link HttpEntityImpl} is designed as not thread-safe and we excepted this should be used in the same thread.
 */
public class HttpEntityImpl implements HttpEntity {

    private Supplier<Class<?>> type = () -> null;
    private Supplier<Type> genericType = () -> null;
    private Supplier<Annotation[]> annotations = () -> null;

    private MediaType mediaType;

    public HttpEntityImpl(MediaType mediaType) {
        this.mediaType = (mediaType != null ? mediaType : MediaType.ALL);
    }

    @Override
    public Class<?> type() {
        return type.get();
    }

    @Override
    public Type genericType() {
        return genericType.get();
    }

    @Override
    public Annotation[] annotations() {
        return annotations.get();
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
    }

    @Override
    public void type(Class<?> type) {
        if (type == null) {
            this.type = () -> null;
            return;
        }
        this.type = () -> type;
    }

    protected void type(Supplier<Class<?>> type) {
        this.type = Checks.checkNotNull(type, "type");
    }

    @Override
    public void genericType(Type genericType) {
        if (genericType == null) {
            this.genericType = () -> null;
            return;
        }
        this.genericType = () -> genericType;
    }

    protected void genericType(Supplier<Type> genericType) {
        this.genericType = Checks.checkNotNull(genericType, "genericType");
    }

    @Override
    public void annotations(Annotation[] annotations) {
        if (annotations == null) {
            this.annotations = () -> null;
            return;
        }
        this.annotations = () -> annotations;
    }

    protected void annotations(Supplier<Annotation[]> annotations) {
        this.annotations = Checks.checkNotNull(annotations, "annotations");
    }

    @Override
    public void mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpEntityImpl{");
        sb.append("type=").append(type());
        sb.append(", genericType=").append(genericType());
        sb.append(", mediaType=").append(mediaType);
        sb.append('}');
        return sb.toString();
    }
}

