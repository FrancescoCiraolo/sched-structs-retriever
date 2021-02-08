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

import java.nio.file.Path;

/**
 * Interface used by {@link Trace Trace} class for return collection of resulting values for each line.
 *
 * Just add a method which has as arg a {@link Request Request} object, and return the got string casted by
 * {@link ValueCaster ValueCaster} provided by {@link Request}
 *
 * @author Francesco Ciraolo
 */
public interface ScrapedOutputLine {

    /**
     * Return the variable associated to the request name, if present.
     *
     * @param requestHeader the request named as in the {@link Trace#startTracing(TraceStreamHandler, Path, String, Request[])} invocation.
     * @param <T> actual type of the variable.
     * @return <code>T</code> object of the variable value.
     */
    <T> T get(RequestHeader<T> requestHeader);
}
