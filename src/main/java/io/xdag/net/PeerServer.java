/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.xdag.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.nio.NioIoHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.xdag.Kernel;
import io.xdag.core.AbstractXdagLifecycle;
import io.xdag.utils.NettyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
public class PeerServer extends AbstractXdagLifecycle {
    private final Kernel kernel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    private final int workerThreadPoolSize = NettyRuntime.availableProcessors() * 2;

    public PeerServer(final Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    protected void doStart() {
        start(kernel.getConfig().getNodeSpec().getNodeIp(), kernel.getConfig().getNodeSpec().getNodePort());
    }

    @Override
    protected void doStop() {
        close();
    }

    public void start(String ip, int port) {
        try {
            // Choose appropriate EventLoopGroup implementation based on OS
            if(SystemUtils.IS_OS_LINUX) {
                //bossGroup = new EpollEventLoopGroup(1); // Set boss thread count to 1
                bossGroup = new MultiThreadIoEventLoopGroup(1, EpollIoHandler.newFactory());
                workerGroup = new MultiThreadIoEventLoopGroup(workerThreadPoolSize, EpollIoHandler.newFactory());


            } else if(SystemUtils.IS_OS_MAC) {
                bossGroup = new MultiThreadIoEventLoopGroup(1, KQueueIoHandler.newFactory()); // Set boss thread count to 1
                workerGroup = new MultiThreadIoEventLoopGroup(workerThreadPoolSize, KQueueIoHandler.newFactory());
            } else {
                bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory()); // Set boss thread count to 1
                workerGroup = new MultiThreadIoEventLoopGroup(workerThreadPoolSize, NioIoHandler.newFactory());
            }

            ServerBootstrap b = NettyUtils.nativeEventLoopGroup(bossGroup, workerGroup);
            
            // Configure TCP parameters
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT);
            b.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, kernel.getConfig().getNodeSpec().getConnectionTimeout());
            
            // Add logging handler
            b.handler(new LoggingHandler());
            b.childHandler(new XdagChannelInitializer(kernel, true, null));

            log.debug("Xdag Node start host:[{}:{}].", ip, port);
            channelFuture = b.bind(ip, port).sync();
        } catch (Exception e) {
            log.error("Xdag Node start error:{}.", e.getMessage(), e);
            // Release resources on exception
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully(); 
            }
        }
    }

    public void close() {
        if (channelFuture != null && channelFuture.channel().isOpen()) {
            try {
                channelFuture.channel().close().sync();
                if (workerGroup != null) {
                    workerGroup.shutdownGracefully();
                }
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
                log.debug("Xdag Node closed.");
            } catch (Exception e) {
                log.error("Xdag Node close error:{}", e.getMessage(), e);
            }
        }
    }

}
