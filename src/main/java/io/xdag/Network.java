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
package io.xdag;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing different network types in XDAG
 */
public enum Network {

    /**
     * Main network for production use
     */
    MAINNET((byte) 0, "mainnet"),

    /**
     * Test network for testing purposes
     */
    TESTNET((byte) 1, "testnet"),

    /**
     * Development network for development purposes
     */
    DEVNET((byte) 2, "devnet");

    /**
     * Constructor for Network enum
     * @param id Network identifier byte
     * @param label Network label string
     */
    Network(byte id, String label) {
        this.id = id;
        this.label = label;
    }

    private final byte id;
    private final String label;

    private static final Map<String, Network> labels = new HashMap<>();
    private static final Map<Byte, Network> ids = new HashMap<>();

    // Initialize static maps
    static {
        for (Network net : Network.values()) {
            labels.put(net.label, net);
            ids.put(net.id, net);
        }
    }

    /**
     * Get network identifier
     * @return Network id byte
     */
    public byte id() {
        return id;
    }

    /**
     * Get network label
     * @return Network label string
     */
    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Get Network enum by network id
     * @param networkId Network identifier byte
     * @return Corresponding Network enum value
     */
    public static Network of(byte networkId) {
        return ids.get(networkId);
    }

    /**
     * Get Network enum by network label
     * @param label Network label string
     * @return Corresponding Network enum value
     */
    public static Network of(String label) {
        return labels.get(label);
    }

}
