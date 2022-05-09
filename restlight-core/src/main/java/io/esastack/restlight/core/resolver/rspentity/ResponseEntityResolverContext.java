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
package io.esastack.restlight.core.resolver.rspentity;

import io.esastack.restlight.core.handler.method.Param;
import io.esastack.restlight.core.context.HttpResponse;
import io.esastack.restlight.core.resolver.HttpEntityResolverContext;
import io.esastack.restlight.core.context.ResponseEntity;
import io.esastack.restlight.core.context.ResponseEntityChannel;

public interface ResponseEntityResolverContext extends HttpEntityResolverContext {

    /**
     * Obtains the {@link Param} to resolve.
     *
     * @return param
     */
    @Override
    ResponseEntity httpEntity();

    /**
     * Obtains teh {@link ResponseEntityChannel}.
     *
     * @return channel
     */
    ResponseEntityChannel channel();

    /**
     * Resolves the {@link HttpResponse#entity()} by given {@link #context()}.
     *
     * @throws Exception exception
     */
    void proceed() throws Exception;

}
