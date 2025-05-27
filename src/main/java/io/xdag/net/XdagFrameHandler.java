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

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.xdag.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XdagFrameHandler extends ByteToMessageCodec<Frame>  {

    private final Config config;

    public XdagFrameHandler(Config config) {
        this.config = config;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame frame, ByteBuf out) throws Exception {
        // Check frame version
        if (frame.getVersion() != Frame.VERSION) {
            log.error("Invalid frame version: {}", frame.getVersion());
            return;
        }

        // Validate body size against configured maximum
        int bodySize = frame.getBodySize();
        if (bodySize < 0 || bodySize > config.getNodeSpec().getNetMaxFrameBodySize()) {
            log.error("Invalid frame body size: {}", bodySize);
            return;
        }

        // Allocate buffer and write frame data
        ByteBuf buf = out.alloc().buffer(Frame.HEADER_SIZE + bodySize);
        frame.writeHeader(buf);
        buf.writeBytes(frame.getBody());

        ctx.write(buf);
        ctx.flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Wait for complete header
        if (in.readableBytes() < Frame.HEADER_SIZE) {
            return;
        }

        // Mark reader index for potential reset
        int readerIndex = in.readerIndex();
        Frame frame = Frame.readHeader(in);

        // Validate frame version
        if (frame.getVersion() != Frame.VERSION) {
            throw new IOException("Invalid frame version: " + frame.getVersion());
        }

        // Validate body size against configured maximum
        int bodySize = frame.getBodySize();
        if (bodySize < 0 || bodySize > config.getNodeSpec().getNetMaxFrameBodySize()) {
            throw new IOException("Invalid frame body size: " + bodySize);
        }

        // Handle incomplete frame
        if (in.readableBytes() < bodySize) {
            in.readerIndex(readerIndex);
            return;
        }

        // Read and set frame body
        byte[] body = new byte[bodySize];
        in.readBytes(body);
        frame.setBody(body);

        // Add decoded frame to output
        out.add(frame);
    }

}
