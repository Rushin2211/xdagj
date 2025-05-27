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

import io.xdag.utils.BasicUtils;

import java.math.BigDecimal;

import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.units.bigints.UInt64;

/**
 * Class representing XDAG amounts with nano precision
 */
public class XAmount implements Comparable<XAmount> {

    // Common constants
    public static final XAmount ZERO = new XAmount(0);
    public static final XAmount ONE = new XAmount(1);
    public static final XAmount TEN = new XAmount(10);

    // Amount in nano units
    private final long nano;

    /**
     * Private constructor to enforce factory methods
     */
    private XAmount(long nano) {
        this.nano = nano;
    }

    /**
     * Create XAmount from long value
     */
    public static XAmount of(long n) {
        return new XAmount(n);
    }

    /**
     * Create XAmount from string value
     */
    public static XAmount of(String n) {
        return new XAmount(Long.parseLong(n));
    }

    /**
     * Create XAmount from bytes
     */
    public static XAmount of(Bytes bytes) {
        return new XAmount(bytes.toLong());
    }

    /**
     * Create XAmount with specified unit
     */
    public static XAmount of(long n, XUnit unit) throws ArithmeticException {
        return new XAmount(Math.multiplyExact(n, unit.factor));
    }

    /**
     * Create XAmount from BigDecimal with specified unit
     */
    public static XAmount of(BigDecimal d, XUnit unit) {
        return new XAmount(d.movePointRight(unit.exp).setScale(0, FLOOR).longValueExact());
    }

    /**
     * Convert to decimal with specified scale and unit
     */
    public BigDecimal toDecimal(int scale, XUnit unit) {
        BigDecimal nano = BigDecimal.valueOf(this.nano);
        return nano.movePointLeft(unit.exp).setScale(scale, FLOOR);
    }

    /**
     * Convert from C-style XDAG amount representation
     */
    public static XAmount ofXAmount(long n) {
        BigDecimal d = BasicUtils.amount2xdagNew(n);
        return new XAmount(d.movePointRight(9).setScale(0, HALF_UP).longValueExact());
    }

    /**
     * Convert to C-style XDAG amount representation
     */
    public UInt64 toXAmount() {
        return BasicUtils.xdag2amount(toDecimal(9, XUnit.XDAG).doubleValue());
    }

    @Override
    public int compareTo(XAmount other) {
        return this.lessThan(other) ? -1 : (this.greaterThan(other) ? 1 : 0);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nano);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof XAmount && ((XAmount) other).nano == nano;
    }

    /**
     * Check if amount is zero
     */
    public boolean isZero() {
        return nano == 0;
    }

    @Override
    public String toString() {
        return String.valueOf(nano);
    }

    /**
     * Comparison methods
     */
    public boolean greaterThan(XAmount other) {
        return nano > other.nano;
    }

    public boolean greaterThanOrEqual(XAmount other) {
        return nano >= other.nano;
    }

    public boolean isPositive() {
        return greaterThan(ZERO);
    }

    public boolean isNotNegative() {
        return greaterThanOrEqual(ZERO);
    }

    public boolean lessThan(XAmount other) {
        return nano < other.nano;
    }

    public boolean lessThanOrEqual(XAmount other) {
        return nano <= other.nano;
    }

    public boolean isNegative() {
        return lessThan(ZERO);
    }

    public boolean isNotPositive() {
        return lessThanOrEqual(ZERO);
    }

    /**
     * Arithmetic operations
     */
    public XAmount negate() throws ArithmeticException {
        return new XAmount(Math.negateExact(this.nano));
    }

    public XAmount add(XAmount a) throws ArithmeticException {
        return new XAmount(Math.addExact(this.nano, a.nano));
    }

    public XAmount subtract(XAmount a) throws ArithmeticException {
        return new XAmount(Math.subtractExact(this.nano, a.nano));
    }

    public XAmount multiply(long a) throws ArithmeticException {
        return new XAmount(Math.multiplyExact(this.nano, a));
    }

    public XAmount multiply(double a) throws ArithmeticException {
        BigDecimal b1 = BigDecimal.valueOf(this.nano);
        BigDecimal b2 = BigDecimal.valueOf(a);
        return XAmount.of(b1.multiply(b2).longValue());
    }

    /**
     * Static helper for adding two amounts
     */
    public static XAmount sum(XAmount a, XAmount b) throws ArithmeticException {
        return new XAmount(Math.addExact(a.nano, b.nano));
    }

}