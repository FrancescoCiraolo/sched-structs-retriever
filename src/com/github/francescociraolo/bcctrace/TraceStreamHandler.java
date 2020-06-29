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

import java.util.stream.Stream;

/**
 * Simple interface to handle a trace stream.
 */
public interface TraceStreamHandler {

    /**
     * Provide a {@link Stream<ScrapedOutputLine>} and allow to work on it. Please note: the stream must be stopped
     * inside this method implementation; otherwise the execution will be endless.
     *
     * @param stream the results' stream of required tracing process
     */
    void streamOperation(Stream<ScrapedOutputLine> stream);
}
