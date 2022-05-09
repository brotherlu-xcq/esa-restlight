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
package io.esastack.restlight.core.locator;

import io.esastack.restlight.core.handler.HandlerMapping;
import io.esastack.restlight.core.handler.HandlerMethodInfo;
import io.esastack.restlight.core.handler.RouteMethodInfo;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * This is used to get an {@link Optional} instance of {@link RouteMethodInfo} from given target {@link
 * Method}.
 */
public interface RouteMethodLocator extends HandlerMethodLocator {

    /**
     * Gets an an {@link Optional} instance of {@link RouteMethodInfo} if possible.
     *
     * @param parent   parent handler mapping if exist, which may be {@code null} for root method.
     * @param userType user type
     * @param method   target method
     * @return optional value of route handler.
     */
    Optional<RouteMethodInfo> getRouteMethodInfo(HandlerMapping parent, Class<?> userType, Method method);

    @Override
    default Optional<HandlerMethodInfo> getHandlerMethodInfo(HandlerMapping parent, Class<?> userType, Method method) {
        return Optional.ofNullable(getRouteMethodInfo(parent, userType, method).orElse(null));
    }

}