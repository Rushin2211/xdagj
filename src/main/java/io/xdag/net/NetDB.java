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

import static io.xdag.utils.BytesUtils.isFullZero;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.xdag.net.node.Node;
import io.xdag.utils.BytesUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class NetDB {

    /**
     * List of remote IP addresses
     */
    List<IP> ipList = Lists.newArrayList();

    public NetDB() {
    }

    /**
     * Create NetDB from data received from remote nodes
     */
    public NetDB(byte[] data) {
        parse(data);
    }

    public void addNewIP(String address) {
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);
        IP newIp = new IP(ip, port);
        if (!ipList.contains(newIp)) {
            ipList.add(newIp);
        }
    }

    public void addNewIP(InetSocketAddress address) {
        IP newIp = new IP(address.getAddress().getHostAddress(),address.getPort());
        if (!ipList.contains(newIp)) {
            ipList.add(newIp);
        }
    }

    public void addNewIP(String ip, int port) {
        IP newIp = new IP(ip, port);
        if (!ipList.contains(newIp)) {
            ipList.add(newIp);
        }
    }

    public void addNewIP(byte[] ip, byte[] port) {
        try {
            IP newIp = new IP(
                    InetAddress.getByAddress(ip),
                    Short.toUnsignedInt(BytesUtils.bytesToShort(port, 0, true)));
            if (!ipList.contains(newIp)) {
                ipList.add(newIp);
            }

        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Add new IP from 6-byte address (4 bytes IP + 2 bytes port)
     */
    public void addNewIP(byte[] address) {
        byte[] ip = BytesUtils.subArray(address, 0, 4);
        byte[] port = BytesUtils.subArray(address, 4, 2);
        if (isFullZero(ip) || isFullZero(port)) {
            return;
        }
        addNewIP(ip, port);
    }

    /**
     * Get list of nodes from received remote IPs
     */
    public Set<Node> getIPList() {
        Set<Node> res = Sets.newHashSet();
        if (!ipList.isEmpty()) {
            for (IP ip : ipList) {
                res.add(new Node(ip.getIp().getHostAddress(), ip.getPort()));
            }
        }
        return res;
    }

    /**
     * Parse byte array into IP list
     *
     * @param data Message content
     */
    public void parse(byte[] data) {
        int size = data.length / 6;
        for (int i = 0; i < size; i++) {
            byte[] ipdata = BytesUtils.subArray(data, i * 6, 6);
            addNewIP(ipdata);
        }
    }

    public byte[] getEncoded() {
        return encode(ipList);
    }

    public byte[] getEncoded(List<IP> input) {
        return encode(input);
    }

    public byte[] encode(List<IP> ipList) {
        int length = ipList.size();
        byte[] res = new byte[length * 6];
        for (int i = 0; i < length; i++) {
            System.arraycopy(ipList.get(i).getData(), 0, res, i * 6, 6);
        }
        return res;
    }

    /**
     * Get number of IPs in the list
     */
    public int getSize() {
        return ipList.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (IP ip : ipList) {
            stringBuilder.append(ip).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Append IPs from another NetDB
     */
    public void appendNetDB(NetDB netDB) {
        if (netDB.ipList.isEmpty()) {
            log.debug("size 0");
            return;
        }
        for (IP ip : netDB.ipList) {
            if (this.ipList.contains(ip)) {
                continue;
            }
            this.ipList.add(ip);
        }
    }

    public boolean contains(InetSocketAddress address) {
        IP ip = new IP(address.getAddress(), address.getPort());
        return this.ipList.contains(ip);
    }

    @Setter
    @Getter
    static class IP {

        InetAddress ip;
        int port;

        public IP(InetAddress ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public IP(String ip, int port) {
            try {
                this.ip = InetAddress.getByName(ip);
                this.port = port;
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
        }

        public byte[] getData() {
            return BytesUtils.merge(ip.getAddress(), BytesUtils.shortToBytes((short) port, true));
        }

        @Override
        public String toString() {
            return ip.toString() + ":" + port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IP ip1 = (IP) o;
            return port == ip1.port && ip.equals(ip1.ip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ip, port);
        }
    }
}
