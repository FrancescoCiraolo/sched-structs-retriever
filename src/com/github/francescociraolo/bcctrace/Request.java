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
 * Key component of {@link Trace Trace}'s
 * {@link Trace#startTracing(TraceStreamHandler, java.nio.file.Path, String, Request[]) startTracing} method.
 * It's used for describe which variables are required from the traced function and their actual types.
 *
 * It's useful for simplify the stream handling with previous defined type.
 *
 * @param <T> the actual type of the requested value, allows to cast it automatically.
 * @author Francesco Ciraolo
 */
public class Request<T> {

    private final String name;
    private final ValueType<T> type;
    private final String var;

    /**
     * Defines a <code>Request</code> for manage a variable from the kernel function.
     *
     * @param name a name used as trace's output template and for retrieving data from lines.
     * @param type the actual type of variable
     * @param var the variable reference, either as function signature name or as references chain.
     */
    public Request(String name, ValueType<T> type, String var) {
        this.name = name;
        this.type = type;
        this.var = var;
    }

    public T castStringOutput(String value) {
        return type.getValueCaster().cast(value);
    }

    public String getFormat() {
        return type.getFormat();
    }

    public String getName() {
        return name;
    }

    public String getVar() {
        return var;
    }

}
