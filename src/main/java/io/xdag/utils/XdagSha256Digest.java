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

package io.xdag.utils;

import org.apache.tuweni.bytes.Bytes;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.util.Arrays;

import java.io.IOException;

/**
 * Utility class for SHA256 hash operations
 */
public class XdagSha256Digest {

    private SHA256Digest sha256Digest;
    private DigestOutputStream outputStream;

    /**
     * Default constructor that initializes SHA256 digest
     */
    public XdagSha256Digest() {
        sha256Init();
    }

    /**
     * Copy constructor
     * @param other The XdagSha256Digest instance to copy from
     */
    public XdagSha256Digest(XdagSha256Digest other) {
        sha256Digest = new SHA256Digest(other.sha256Digest);
        outputStream = new DigestOutputStream(sha256Digest);
    }

    /**
     * Initialize SHA256 digest and output stream
     */
    public void sha256Init() {
        sha256Digest = new SHA256Digest();
        outputStream = new DigestOutputStream(sha256Digest);
    }

    /**
     * Update the digest with input bytes
     * @param in Input bytes to update
     */
    public void sha256Update(Bytes in) throws IOException {
        outputStream.write(in.toArray());
    }

    /**
     * Perform double SHA256 hash and return reversed result
     * @param in Input bytes to hash
     * @return Reversed double SHA256 hash result
     */
    public byte[] sha256Final(Bytes in) throws IOException {
        outputStream.write(in.toArray());
        byte[] hash = outputStream.getDigest();
        sha256Digest.reset();
        outputStream.write(hash);
        byte[] origin = outputStream.getDigest();
        origin = Arrays.reverse(origin);
        return origin;
    }

    /**
     * Get the state that can be sent to C implementation
     * Extracts 32 bytes from encoded state and converts endianness
     * @return 32 bytes state with adjusted endianness
     */
    public byte[] getState() {
        byte[] encodedState = sha256Digest.getEncodedState();
        byte[] state = new byte[32];
        System.arraycopy(encodedState, encodedState.length - 32 - 4, state, 0, 32);
        for (int i = 0; i < 32; i += 4) {
            int temp = BytesUtils.bytesToInt(state, i, false);
            System.arraycopy(BytesUtils.intToBytes(temp, true), 0, state, i, 4);
        }
        return state;
    }

}
