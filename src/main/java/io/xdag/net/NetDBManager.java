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

import io.xdag.config.Config;
import io.xdag.config.DevnetConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

/**
 * Manages the network database including whitelisted nodes
 */
@Getter
@Slf4j
public class NetDBManager {
    private final String database;
    private final String databaseWhite;
    private final NetDB whiteDB;
    private final Config config;

    /**
     * Constructor initializes database paths and network DBs
     */
    public NetDBManager(Config config) {
        this.config = config;
        database = config.getNodeSpec().getNetDBDir();
        databaseWhite = config.getNodeSpec().getWhiteListDir();
        whiteDB = new NetDB();
    }

    /**
     * Load whitelist IPs from config
     */
    public void loadFromConfig() {
        for (InetSocketAddress address:config.getNodeSpec().getWhiteIPList()){
            whiteDB.addNewIP(address);
        }
    }

    /**
     * Initialize network database
     */
    public void start() {
        loadFromConfig();
    }

    /**
     * Check if address is whitelisted
     */
    public boolean canAccept(InetSocketAddress address) {
        return whiteDB.contains(address);
    }

}
