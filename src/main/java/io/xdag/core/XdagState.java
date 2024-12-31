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

public enum XdagState {

    /**
     * Pool initialization state
     */
    INIT(0x00),
    /**
     * Wallet key generation state
     */
    KEYS(0x01),
    /**
     * Local storage corruption state - resetting blocks engine
     */
    REST(0x02),
    /**
     * Loading blocks from local storage state
     */
    LOAD(0x03),
    /**
     * Blocks loaded state - waiting for run command
     */
    STOP(0x04),
    /**
     * Attempting to connect to dev network
     */
    WDST(0x05),
    /**
     * Attempting to connect to test network
     */
    WTST(0x06),
    /**
     * Attempting to connect to main network
     */
    WAIT(0x07),

    /**
     * Connected to dev network - synchronizing
     */
    CDST(0x08),
    /**
     * Connected to dev network - synchronizing from low to high
     */
    CDSTP(0x10),
    /**
     * Connected to test network - synchronizing
     */
    CTST(0x09),
    /**
     * Connected to test network - synchronizing from low to high
     */
    CTSTP(0x11),
    /**
     * Connected to main network - synchronizing
     */
    CONN(0x0a),
    /**
     * Connected to main network - synchronizing from low to high
     */
    CONNP(0x12),

    /**
     * Synchronized with dev network - normal testing
     */
    SDST(0x0b),
    /**
     * Synchronized with test network - normal testing
     */
    STST(0x0c),
    /**
     * Synchronized with main network - normal operation
     */
    SYNC(0x0d),

    /**
     * Transfer in progress state
     */
    XFER(0x0e);

    private int cmd;
    private int temp;

    XdagState(int cmd) {
        this.cmd = cmd;
        this.temp = -1;
    }

    public byte asByte() {
        return (byte) cmd;
    }

    public void setState(XdagState state) {
        this.cmd = state.asByte();
    }

    public void tempSet(XdagState state) {
        this.temp = this.cmd;
        this.cmd = state.asByte();
    }

    public void rollback() {
        this.cmd = this.temp;
        this.temp = -1;
    }

    @Override
    public String toString() {
        return switch (cmd) {
            case 0x00 -> "Pool Initializing....";
            case 0x01 -> "Generating keys...";
            case 0x02 -> "The local storage is corrupted. Resetting blocks engine.";
            case 0x03 -> "Loading blocks from the local storage.";
            case 0x04 -> "Blocks loaded. Waiting for 'run' command.";
            case 0x05 -> "Trying to connect to the dev network.";
            case 0x06 -> "Trying to connect to the test network.";
            case 0x07 -> "Trying to connect to the main network.";
            case 0x08 -> "Connected to the dev network. Synchronizing.";
            case 0x09 -> "Connected to the test network. Synchronizing.";
            case 0x0a -> "Connected to the main network. Synchronizing.";
            case 0x0b -> "Synchronized with the dev network. Normal testing.";
            case 0x0c -> "Synchronized with the test network. Normal testing.";
            case 0x0d -> "Synchronized with the main network. Normal operation.";
            case 0x0e -> "Waiting for transfer to complete.";
            case 0x10 -> "Connected to the dev network. Synchronizing from low to high.";
            case 0x11 -> "Connected to the test network. Synchronizing from low to high.";
            case 0x12 -> "Connected to the main network. Synchronizing from low to high.";
            default -> "Abnormal State";
        };
    }
}
