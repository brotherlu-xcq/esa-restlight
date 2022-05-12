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
import io.esastack.restlight.core.DeployContext;
import io.esastack.restlight.core.context.RequestContext;
import io.esastack.restlight.core.resolver.ResolverContext;

public class ParamResolverContextImpl implements ParamResolverContext {

    private final RequestContext requestContext;
    private final DeployContext deployContext;

    public ParamResolverContextImpl(ResolverContext resolverContext) {
        this(resolverContext.requestContext(), resolverContext.deployContext());
    }

    public ParamResolverContextImpl(RequestContext requestContext, DeployContext deployContext) {
        Checks.checkNotNull(requestContext, "requestContext");
        this.requestContext = requestContext;
        this.deployContext = deployContext;
    }

    @Override
    public RequestContext requestContext() {
        return requestContext;
    }

    @Override
    public DeployContext deployContext() {
        return deployContext;
    }
}
