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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import io.xdag.core.XdagStats;
import io.xdag.net.message.consensus.SumRequestMessage;
import io.xdag.utils.BytesUtils;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.MutableBytes;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class NetTest {

    @Test
    public void testNetDB() {
        String expected = "7f000001611e7f0000015f767f000001d49d7f000001b822";
        NetDB netDB = new NetDB();
        String first = "7f000001611e";
        String second = "7f0000015f76";
        String third = "7f000001d49d";
        String fourth = "7f000001b822";
        String five = "7f000001b822";

        int size = 4;
        netDB.addNewIP(Hex.decode(first));
        netDB.addNewIP(Hex.decode(second));
        netDB.addNewIP(Hex.decode(third));
        netDB.addNewIP(Hex.decode(fourth));
        netDB.addNewIP(Hex.decode(five));

        assertEquals(size, netDB.getSize());

        assertEquals(expected, Hex.toHexString(netDB.getEncoded()));

        String all = "7f000001611e7f0000015f767f000001" + "d49d7f000001b822";

        NetDB netDB1 = new NetDB(Hex.decode(all));
        assertEquals(size, netDB1.getSize());
        assertEquals(all, Hex.toHexString(netDB1.getEncoded()));

        MutableBytes mutableBytes = MutableBytes.create(112);
        long nhosts = netDB.getIPList().size();
        long totalHosts = netDB.getIPList().size();

        mutableBytes.set(0, Bytes.wrap(BytesUtils.longToBytes(nhosts, true)));
        mutableBytes.set(8, Bytes.wrap(BytesUtils.longToBytes(totalHosts, true)));
        mutableBytes.set(16, Bytes.wrap(netDB.getEncoded()));
        assertEquals(mutableBytes.toHexString(),
                "0x040000000000000004000000000000007f000001611e7f0000015f767f000001d49d7f000001b822000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

    }

    @Test
    public void testNetUpdate() {
        String ip = "127.0.0.1:40404";
        NetDB netDB = new NetDB();
        netDB.addNewIP(ip);
        assertEquals(1, netDB.getSize());
        assertEquals("7f000001d49d", Hex.toHexString(netDB.getEncoded()));
    }

    @Test
    public void testNetDBParse() {
        long starttime = 1600616700000L;
        long endtime = starttime + 10000;

        XdagStats stats = new XdagStats();
        stats.maxdifficulty = BigInteger.TWO;
        stats.totalnblocks = 10001;
        stats.totalnmain = 1001;
        stats.totalnhosts = 11;
        stats.maintime = endtime;

        SumRequestMessage sumRequestMessage = new SumRequestMessage(starttime, endtime, stats);

        String hexString = Bytes.wrap(sumRequestMessage.getBody()).toHexString();

        sumRequestMessage = new SumRequestMessage(Bytes.fromHexString(hexString).toArray());

        assertEquals(starttime, sumRequestMessage.getStarttime());
        assertEquals(endtime, sumRequestMessage.getEndtime());

        XdagStats xdagStats = sumRequestMessage.getXdagStats();
        assertEquals(BigInteger.TWO, xdagStats.maxdifficulty);
        assertEquals(stats.totalnblocks, xdagStats.totalnblocks);
        assertEquals(stats.totalnmain, xdagStats.totalnmain);
        assertEquals(stats.totalnhosts, xdagStats.totalnhosts);
        assertEquals(stats.maintime, xdagStats.maintime);
    }

}
