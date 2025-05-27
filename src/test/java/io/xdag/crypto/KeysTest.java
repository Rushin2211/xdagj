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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.xdag.utils.Numeric;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.crypto.KeyPair;
import org.junit.Test;

public class KeysTest {

    private static final byte[] ENCODED;

    static {
        byte[] privateKey = Numeric.hexStringToByteArray(SampleKeys.PRIVATE_KEY_STRING);
        byte[] publicKey = Numeric.hexStringToByteArray(SampleKeys.PUBLIC_KEY_STRING);
        ENCODED = Arrays.copyOf(privateKey, privateKey.length + publicKey.length);
        System.arraycopy(publicKey, 0, ENCODED, privateKey.length, publicKey.length);
    }

    @Test
    public void testCreateSecp256k1KeyPair() throws Exception {
        KeyPair keyPair = Keys.createSecp256k1KeyPair();
        PrivateKey privateKey = keyPair.getPrivateKey();
        PublicKey publicKey = keyPair.getPublicKey();

        assertNotNull(privateKey);
        assertNotNull(publicKey);

        assertEquals((32), privateKey.getEncoded().length);
        assertEquals((64), publicKey.getEncoded().length);
    }

    @Test
    public void testCreateEcKeyPair()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair key = Keys.createEcKeyPair();
        assertEquals((1), key.getPublicKey().getEncodedBytes().toUnsignedBigInteger().signum());
        assertEquals((1), key.getPrivateKey().getEncodedBytes().toUnsignedBigInteger().signum());
    }

    @Test
    public void testTransform()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        for (int i = 0; i < 1000; i++) {
            KeyPair key = Keys.createEcKeyPair();
            assertEquals(Bytes.wrap(key.getPublicKey().asEcPoint(Sign.CURVE).getEncoded(true)),
                    Bytes.wrap(Sign.publicKeyBytesFromPrivate(key.getPrivateKey().getEncodedBytes().toUnsignedBigInteger(), true)));
            assertEquals(Bytes.wrap(key.getPublicKey().asEcPoint(Sign.CURVE).getEncoded(false)),
                    Bytes.wrap(Sign.publicKeyBytesFromPrivate(key.getPrivateKey().getEncodedBytes().toUnsignedBigInteger(), false)));
        }
    }

}
