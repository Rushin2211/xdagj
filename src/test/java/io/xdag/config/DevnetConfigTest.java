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

package io.xdag.config;

import static io.xdag.core.XdagField.FieldType.XDAG_FIELD_HEAD_TEST;
import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import io.xdag.core.XUnit;

public class DevnetConfigTest {

    private Config config;

    @Before
    public void setUp() {
        config = new DevnetConfig();
    }

    @Test
    public void testParams() {
        assertEquals("devnet", config.getRootDir());
        assertEquals("xdag-devnet", config.getConfigName());
        assertEquals(StringUtils.EMPTY, config.getNodeSpec().getWhitelistUrl());
        assertEquals(0x16900000000L, config.getXdagEra());
        assertEquals(XDAG_FIELD_HEAD_TEST, config.getXdagFieldHeader());
        assertEquals(1000, config.getApolloForkHeight());
        assertEquals("1024.0", String.valueOf(config.getMainStartAmount().toDecimal(1, XUnit.XDAG)));
        assertEquals("128.0", String.valueOf(config.getApolloForkAmount().toDecimal(1, XUnit.XDAG)));
        assertEquals(8, config.getNodeSpec().getMaxInboundConnectionsPerIp());
        assertEquals(888L, config.getTxPageSizeLimit());
    }
}
