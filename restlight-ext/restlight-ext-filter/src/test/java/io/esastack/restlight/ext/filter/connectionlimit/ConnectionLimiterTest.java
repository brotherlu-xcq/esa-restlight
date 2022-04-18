/*
 * Copyright 2020 OPPO ESA Stack Project
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
package io.esastack.restlight.ext.filter.connectionlimit;

import com.google.common.util.concurrent.RateLimiter;
import io.esastack.restlight.server.handler.Connection;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectionLimiterTest {

    @Test
    void testLimited() throws Exception {
        final ConnectionLimitOptions ops = ConnectionLimitOptionsConfigure.newOpts()
                .maxPerSecond(1).configured();
        final RateLimiter limiter0 = mock(RateLimiter.class);
        final ConnectionLimiter limiter = new ConnectionLimiter(ops, limiter0);
        final Connection connection = mock(Connection.class);
        when(limiter0.tryAcquire(1)).thenReturn(true);
        limiter.onConnectionInit(connection);

        when(limiter0.tryAcquire(1)).thenReturn(false);
        limiter.onConnectionInit(connection);
        verify(connection).close();
    }

}
