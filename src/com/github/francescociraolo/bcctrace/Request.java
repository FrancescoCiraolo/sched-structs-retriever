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
 * {@link Trace#startTracing(TraceStreamHandler, java.util.Collection, String, Request[]) startTracing} method.
 * It's used for describe which variables are required from the traced function and their actual types.
 *
 * It's useful for simplify the stream handling with previous defined type.
 *
 * @param <T> the actual type of the requested value, allows to cast it automatically.
 * @author Francesco Ciraolo
 */
public interface Request<T> extends RequestHeader<T> {

    static <T> Request<T> getSimpleRequest(String name, ValueType<T> valueType, String variableReference) {
        var header = new SimpleRequestHeader<>(name, valueType);
        return getSimpleRequest(header, variableReference);
    }

    static <T> Request<T> getSimpleRequest(RequestHeader<T> header, String variableReference) {
        return new SimpleRequest<>(header, variableReference);
    }

    /**
     * Returns the {@link RequestHeader} of this request.
     * Commonly could be this same object, but this also allow a common header among multiple requests.
     *
     * @return the {@link RequestHeader} of this Request.
     */
    RequestHeader<T> getRequestHeader();

    /**
     * The variable reference used by {@link Trace}.
     *
     * @return the {@link String} of the variable's reference.
     */
    String getVariableReference();

}
