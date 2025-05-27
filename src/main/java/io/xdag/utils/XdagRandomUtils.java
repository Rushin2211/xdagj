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

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import java.security.SecureRandom;

/**
 * Utility class for generating random numbers and bytes using XO-RO-SHI-RO-128-PP algorithm
 */
public class XdagRandomUtils {

    /**
     * Random number generator instance using XO-RO-SHI-RO-128-PP algorithm
     */
    private final static UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    /**
     * Generate a random integer between 0 (inclusive) and n (exclusive)
     * @param n Upper bound (exclusive)
     * @return Random integer value in [0, n)
     */
    public static int nextInt(int n) {
        return rng.nextInt(n);
    }

    /**
     * Generate a random long between 0 (inclusive) and n (exclusive)
     * @param n Upper bound (exclusive)
     * @return Random long value in [0, n)
     */
    public static long nextLong(long n) {
        return rng.nextLong(n);
    }

    /**
     * Create and return a new byte array filled with random bytes
     * @param count Size of the byte array to create
     * @return New byte array filled with random values
     */
    public static byte[] nextNewBytes(int count) {
        final byte[] result = new byte[count];
        rng.nextBytes(result);
        return result;
    }

    /**
     * Create and return a random byte array
     * @param count Size of the byte array to create
     * @return Random byte array
     */
    public static byte[] generateRandomBytes(int count) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[count];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

}
