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
package io.xdag.core;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing snapshot information
 */
@Getter
@Setter
public class SnapshotInfo {

    // Type of snapshot data: true for PUBKEY, false for BLOCK_DATA
    protected boolean type;
    // The actual data - either block data or public key
    protected byte[] data;

    /**
     * Default constructor
     */
    public SnapshotInfo() {
    }

    /**
     * Constructor with type and data
     * @param type Type of data (true=PUBKEY, false=BLOCK_DATA)
     * @param data The snapshot data
     */
    public SnapshotInfo(boolean type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Get the type of snapshot data
     * @return true for PUBKEY, false for BLOCK_DATA
     */
    public boolean getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SnapshotInfo{" +
                "type=" + type +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
