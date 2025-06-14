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

package io.xdag.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Aes {

    private Aes() {
        // Private constructor to prevent instantiation
    }

    private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data)
            throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        // Create output buffer
        int size = cipher.getOutputSize(data.length);
        byte[] buf = new byte[size];

        // Process data
        int length1 = cipher.processBytes(data, 0, data.length, buf, 0);
        int length2 = cipher.doFinal(buf, length1);
        int length = length1 + length2;

        // Copy buffer to result, without padding
        byte[] result = new byte[length];
        System.arraycopy(buf, 0, result, 0, result.length);

        return result;
    }

    /**
     * Encrypt data with AES/CBC/PKCS5Padding.
     *
     * @param raw The raw data to be encrypted
     * @param key The encryption key
     * @param iv The initialization vector
     * @return The encrypted data
     */
    public static byte[] encrypt(byte[] raw, byte[] key, byte[] iv) {
        try {
            BlockCipher blockCipher = AESEngine.newInstance();
            CBCModeCipher cbcModeCipher = CBCBlockCipher.newInstance(blockCipher);
            PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(cbcModeCipher);
            CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
            aes.init(true, params);

            return cipherData(aes, raw);
        } catch (DataLengthException | IllegalArgumentException | IllegalStateException
                | InvalidCipherTextException e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt data with AES/CBC/PKCS5Padding
     *
     * @param encrypted The encrypted data
     * @param key The decryption key
     * @param iv The initialization vector
     * @return The decrypted data
     */
    public static byte[] decrypt(byte[] encrypted, byte[] key, byte[] iv) {
        try {
            BlockCipher blockCipher = AESEngine.newInstance();
            CBCModeCipher cbcModeCipher = CBCBlockCipher.newInstance(blockCipher);
            PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(cbcModeCipher);
            CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
            aes.init(false, params);

            return cipherData(aes, encrypted);
        } catch (DataLengthException | IllegalArgumentException | IllegalStateException
                | InvalidCipherTextException e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
