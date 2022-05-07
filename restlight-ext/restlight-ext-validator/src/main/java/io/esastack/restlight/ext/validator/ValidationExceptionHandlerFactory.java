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
package io.esastack.restlight.ext.validator;

import esa.commons.annotation.Internal;
import esa.commons.spi.Feature;
import io.esastack.restlight.core.DeployContext;
import io.esastack.restlight.core.util.Constants;
import io.esastack.restlight.core.dispatcher.ExceptionHandlerChain;
import io.esastack.restlight.core.dispatcher.IExceptionHandler;
import io.esastack.restlight.core.context.RequestContext;
import io.esastack.restlight.core.spi.ExceptionHandlerFactory;
import io.esastack.restlight.core.util.ErrorDetail;
import io.netty.util.internal.InternalThreadLocalMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Internal
@Feature(tags = Constants.INTERNAL)
public class ValidationExceptionHandlerFactory implements ExceptionHandlerFactory {

    @Override
    public Optional<IExceptionHandler> handler(DeployContext ctx) {
        return Optional.of(new ValidationExceptionHandler());
    }

    private static class ValidationExceptionHandler implements IExceptionHandler {

        @Override
        public CompletionStage<Void> handle(RequestContext context, Throwable th,
                                            ExceptionHandlerChain next) {
            final CompletableFuture<Void> handled = new CompletableFuture<>();
            next.handle(context, th).whenComplete((v, ex) -> {
                if (ex == null) {
                    handled.complete(null);
                    return;
                }
                if (ex instanceof ConstraintViolationException) {
                    Set<ConstraintViolation<?>> constraints = ((ConstraintViolationException) ex)
                            .getConstraintViolations();
                    if (constraints == null || constraints.isEmpty()) {
                        context.response().entity(new ErrorDetail<>(context.request().path(), ex.getMessage()));
                    } else {
                        List<ConstraintDetail> details = InternalThreadLocalMap.get().arrayList(constraints.size());
                        for (ConstraintViolation<?> c : constraints) {
                            details.add(new ConstraintDetail(c.getPropertyPath().toString(),
                                    c.getInvalidValue(), c.getMessage()));
                        }
                        context.response().entity(new ErrorDetail<>(context.request().path(), details));
                    }
                    handled.complete(null);
                } else {
                    handled.completeExceptionally(ex);
                }
            }).exceptionally(ex -> {
                handled.completeExceptionally(ex);
                return null;
            });
            return handled;
        }

        @Override
        public int getOrder() {
            return 100;
        }

    }
}

