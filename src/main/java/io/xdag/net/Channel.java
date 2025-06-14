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

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.xdag.Kernel;
import io.xdag.net.message.MessageQueue;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * Channel represents a network connection between two peers in the XDAG network
 */
@Getter
@Setter
public class Channel {

    private SocketChannel socket;
    private boolean isInbound;
    private InetSocketAddress remoteAddress;
    private Peer remotePeer;
    private MessageQueue msgQueue;
    private boolean isActive;
    private XdagP2pHandler p2pHandler;

    /**
     * Creates a new channel instance with the given socket
     * 
     * @param socket The socket channel for network communication
     */
    public Channel(SocketChannel socket) {
        this.socket = socket;
    }

    /**
     * Initializes the channel with pipeline handlers and network settings
     * 
     * @param pipe Pipeline to add handlers to
     * @param isInbound Whether this is an inbound connection
     * @param remoteAddress Remote peer's address
     * @param kernel Reference to the main kernel
     */
    public void init(ChannelPipeline pipe, boolean isInbound, InetSocketAddress remoteAddress, Kernel kernel) {
        this.isInbound = isInbound;
        this.remoteAddress = remoteAddress;
        this.remotePeer = null;

        this.msgQueue = new MessageQueue(kernel.getConfig());

        // Register channel handlers
        if (isInbound) {
            pipe.addLast("inboundLimitHandler",
                    new ConnectionLimitHandler(kernel.getConfig().getNodeSpec().getNetMaxInboundConnectionsPerIp()));
        }
        pipe.addLast("readTimeoutHandler", new ReadTimeoutHandler(kernel.getConfig().getNodeSpec().getNetChannelIdleTimeout(), TimeUnit.MILLISECONDS));
        pipe.addLast("xdagFrameHandler", new XdagFrameHandler(kernel.getConfig()));
        pipe.addLast("xdagMessageHandler", new XdagMessageHandler(kernel.getConfig()));
        p2pHandler = new XdagP2pHandler(this, kernel);
        pipe.addLast("xdagP2pHandler", p2pHandler);
    }

    /**
     * Closes the socket connection
     */
    public void close() {
        socket.close();
    }

    /**
     * Gets the message queue for this channel
     */
    public MessageQueue getMessageQueue() {
        return msgQueue;
    }

    /**
     * Checks if this is an inbound connection
     */
    public boolean isInbound() {
        return isInbound;
    }

    /**
     * Checks if this is an outbound connection
     */
    public boolean isOutbound() {
        return !isInbound();
    }

    /**
     * Checks if the channel is active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Activates the channel with the given remote peer
     * 
     * @param remotePeer The remote peer to activate with
     */
    public void setActive(Peer remotePeer) {
        this.remotePeer = remotePeer;
        this.isActive = true;
    }

    /**
     * Deactivates the channel
     */
    public void setInactive() {
        this.isActive = false;
    }

    /**
     * Gets the remote peer's IP address
     */
    public String getRemoteIp() {
        return remoteAddress.getAddress().getHostAddress();
    }

    /**
     * Gets the remote peer's port number
     */
    public int getRemotePort() {
        return remoteAddress.getPort();
    }

    @Override
    public String toString() {
        return "Channel [" + (isInbound ? "Inbound" : "Outbound") + ", remoteIp = " + getRemoteIp() + ", remotePeer = "
                + remotePeer + "]";
    }
}
