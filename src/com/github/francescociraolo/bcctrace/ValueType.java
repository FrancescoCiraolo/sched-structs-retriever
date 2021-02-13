package com.github.francescociraolo.bcctrace;

/*
 * Sched Struct Retriever
 * Copyright (C) 2020 Francesco Ciraolo
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A <code>ValueType</code> object just match a c-style pattern and an caster method; it is used for define the type
 * of a request variable for {@link Trace}.
 *
 * Contains also some frequent <code>ValueType</code>s, as, for example:<br>
 *      int [format="%d", {@link ValueCaster}={@link Integer#parseInt(String)}]<br>
 *          or<br>
 *      a bit mask which as "%lu" as format and a private method for return the obtained long as the 1 positions in
 *          the binary representation <code>int[]</code>.
 *
 * @param <T>
 */
public class ValueType<T> {

    /**
     * Integer variable type.
     */
    public static final ValueType<Integer> D = new ValueType<>("%d", Integer::parseInt);
    /**
     * Unsigned long variable type.
     */
    public static final ValueType<Long> LU = new ValueType<>("%lu", Long::parseUnsignedLong);
    /**
     * Bit mask variable type.
     */
    public static final ValueType<int[]> BIT_ARRAY_VALUES = new ValueType<>("%lu", BitArray::valuesFromIntString);
    /**
     * String variable type.
     */
    public static final ValueType<String> S = new ValueType<>("%s", s -> s);
    /**
     * Unsigned integer variable type.
     */
    public static final ValueType<Integer> U = new ValueType<>("%u", Integer::parseUnsignedInt);

    private final String format;
    private final ValueCaster<T> valueCaster;

    /**
     * @param format the c-style printf format of the variable type.
     * @param valueCaster a function returning a <code>T</code> object from the <code>s</code> value.
     */
    public ValueType(String format, ValueCaster<T> valueCaster) {
        this.format = format;
        this.valueCaster = valueCaster;
    }

    public String getFormat() {
        return format;
    }

    public ValueCaster<T> getValueCaster() {
        return valueCaster;
    }
}
