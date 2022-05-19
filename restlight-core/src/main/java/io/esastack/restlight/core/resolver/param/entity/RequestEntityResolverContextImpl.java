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

package io.esastack.restlight.core.resolver.param.entity;

import io.esastack.restlight.core.context.RequestContext;
import io.esastack.restlight.core.context.RequestEntity;

public class RequestEntityResolverContextImpl implements RequestEntityResolverContext {

    private final RequestEntity httpEntity;
    private final RequestContext requestContext;

    public RequestEntityResolverContextImpl(RequestEntity httpEntity, RequestContext requestContext) {
        this.httpEntity = httpEntity;
        this.requestContext = requestContext;
    }

    @Override
    public RequestEntity requestEntity() {
        return httpEntity;
    }

    @Override
    public RequestContext requestContext() {
        return requestContext;
    }
}
