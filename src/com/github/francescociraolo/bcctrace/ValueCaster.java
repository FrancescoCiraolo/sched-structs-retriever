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
 * Functional interface representing a casting function for read <code>String</code> as previous defined types.
 *
 * @param <T> type of resulting value of this cast function
 * @author Francesco Ciraolo
 */
@FunctionalInterface
public interface ValueCaster<T> {

    /**
     * Return the passed <code>String</code> as a <code>T</code> object.
     *
     * @param s the input <code>String</code>
     * @return a <code>T</code> object representing the <code>s</code> value
     */
    T cast(String s);
}
