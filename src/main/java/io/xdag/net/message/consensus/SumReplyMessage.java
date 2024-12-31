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
package io.xdag.net.message.consensus;

import org.apache.tuweni.bytes.MutableBytes;

import io.xdag.utils.SimpleEncoder;
import io.xdag.core.XdagStats;
import io.xdag.net.message.MessageCode;
import io.xdag.utils.SimpleDecoder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SumReplyMessage extends XdagMessage {

    MutableBytes sum;

    public SumReplyMessage(long endtime, long random, XdagStats xdagStats, MutableBytes sum) {
        super(MessageCode.SUMS_REPLY, null, 1, endtime, random, xdagStats);

        SimpleEncoder enc = super.encode();
        enc.writeBytes(sum.toArray());
        this.body = enc.toBytes();
    }

    public SumReplyMessage(byte[] body) {
        super(MessageCode.SUMS_REPLY, null, body);

        SimpleDecoder dec = super.decode();
        this.sum = MutableBytes.wrap(dec.readBytes());
    }

}
