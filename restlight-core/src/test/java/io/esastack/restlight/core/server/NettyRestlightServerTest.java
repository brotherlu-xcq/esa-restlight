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
package io.esastack.restlight.core.server;

import esa.commons.NetworkUtils;
import io.esastack.restlight.core.util.RestlightVer;
import io.esastack.restlight.core.config.BizThreadsOptionsConfigure;
import io.esastack.restlight.core.config.ServerOptions;
import io.esastack.restlight.core.config.ServerOptionsConfigure;
import io.esastack.restlight.core.config.SslOptionsConfigure;
import io.esastack.restlight.core.server.processor.RestlightHandler;
import io.esastack.restlight.core.server.processor.schedule.ExecutorScheduler;
import io.esastack.restlight.core.server.processor.schedule.Schedulers;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpServerCodec;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NettyRestlightServerTest {

    @Test
    void testBootstrapServer() {
        final ServerOptions options = ServerOptionsConfigure.newOpts()
                .ioThreads(1)
                .bizThreads(BizThreadsOptionsConfigure.newOpts().core(0).max(1).configured())
                .ssl(SslOptionsConfigure.newOpts()
                        .enable(true)
                        .ciphers(Collections.singletonList("xx"))
                        .enabledProtocols(Collections.singletonList("xx"))
                        .certChainPath("/abc")
                        .keyPath("/xyz")
                        .keyPassword("mn")
                        .trustCertsPath("xxx")
                        .sessionTimeout(180L)
                        .sessionCacheSize(64L)
                        .handshakeTimeoutMillis(3000L)
                        .configured())
                .configured();


        final SocketAddress address = new InetSocketAddress(NetworkUtils.selectRandomPort());
        final RestlightHandler handler = mock(RestlightHandler.class);

        final int randomPort = NetworkUtils.selectRandomPort();
        final RestlightServer server = RestlightServerBootstrap.from(options, handler)
                .daemon(true)
                .withAddress(randomPort)
                .withDomainSocketAddress("/abc")
                .withAddress("127.0.0.1", randomPort)
                .withAddress(address)
                .withOption(ChannelOption.AUTO_CLOSE, true)
                .withOptions(null)
                .withOptions(Collections.emptyMap())
                .withOptions(Collections.singletonMap(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT))
                .withChildOption(ChannelOption.AUTO_CLOSE, true)
                .withChildOptions(null)
                .withChildOptions(Collections.emptyMap())
                .withChildOptions(Collections.singletonMap(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT))
                .withChannelHandler(new HttpServerCodec())
                .withChannelHandlers(Collections.singletonList(new HttpServerCodec()))
                .forServer();

        assertEquals(address, server.address());
        server.await();
        assertFalse(server.isStarted());
        assertEquals(RestlightVer.version(), server.version());
        assertNull(server.ioExecutor());
        when(handler.schedulers()).thenReturn(Collections.singletonList(Schedulers.biz()));
        assertEquals(((ExecutorScheduler) Schedulers.biz()).executor(), server.bizExecutor());

        try {
            server.start();
            assertTrue(server.isStarted());
            assertNotNull(server.ioExecutor());
            server.shutdown();
        } catch (Throwable ignored) {
        }
    }

}
