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
package io.esastack.restlight.core.resolver.param;

import esa.commons.Checks;
import io.esastack.restlight.core.context.RequestContext;

import java.util.List;

public class HttpParamResolverContextImpl implements HttpParamResolverContext {

    private final RequestContext context;
    private final HttpParamResolver resolver;
    private final HttpParamResolverAdvice[] advices;
    private int index;

    public HttpParamResolverContextImpl(RequestContext context,
                                        HttpParamResolver resolver,
                                        List<HttpParamResolverAdvice> advices) {
        Checks.checkNotNull(context, "context");
        Checks.checkNotNull(resolver, "resolver");
        this.context = context;
        this.resolver = resolver;
        this.advices = (advices == null ? null : advices.toArray(new HttpParamResolverAdvice[0]));
    }

    @Override
    public RequestContext requestContext() {
        return context;
    }

    @Override
    public Object proceed() throws Exception {
        if (advices == null || index >= advices.length) {
            return resolver.resolve(context);
        }

        return advices[index++].aroundResolve(this);
    }
}