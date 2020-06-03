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

import java.io.Closeable;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Trace process wrapper, useful for close the tracing when needed and as {@link ScrapedOutputLine}'s {@link Stream}
 * provider.
 *
 * @author francesco Ciraolo
 */
public class Tracing implements Closeable {

    /**
     * Pattern to retrieve the results from trace lines.
     */
    private static final Pattern PATTERN0 = Pattern.compile("\\[([^=]*)=([^]]*)]");

    /**
     * trace's script process reference.
     */
    private final Process process;
    /**
     * The {@link Stream} created in the constructor and usable for retrieving data.
     */
    private final Stream<ScrapedOutputLine> stream;

    /**
     * @param process the {@link Process}'s reference, for future close operation
     */
    Tracing(Process process) {
        this.process = process;
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

        var scanner = new Scanner(process.getInputStream());
        //The stream is generated requiring lines from trace's process and extracting with extractResults method
        this.stream = Stream
                .generate(() -> {
                    synchronized (scanner) {
                        return scanner.nextLine();
                    }
                })
                .map(Tracing::extractResults)
                .flatMap(Optional::stream);
    }

    @Override
    public void close() {
        process.destroy();
    }

    /**
     * Just use the pattern for match <code>s</code> and put each name-value pair into a {@link ScrapedOutputLine}.
     *
     * @param s a trace's scripts output line.
     * @return a {@link Optional} containing the {@link ScrapedOutputLine} extracted from line if found or an empty one otherwise.
     */
    private static Optional<ScrapedOutputLine> extractResults(String s) {
        var map = new HashMap<String, String>();
        var matcher = PATTERN0.matcher(s);
        while (matcher.find()) map.put(matcher.group(1), matcher.group(2));
        if (map.isEmpty()) return Optional.empty();
        return Optional.of(new ScrapedOutputLineMap(map));
    }

    public Stream<ScrapedOutputLine> getStream() {
        return stream;
    }

    /**
     * Simple implementation of {@link ScrapedOutputLine} using an HashMap to store values.
     */
    private static class ScrapedOutputLineMap implements ScrapedOutputLine {

        private final HashMap<String, String> values;

        public ScrapedOutputLineMap(HashMap<String, String> values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            var eq = o instanceof ScrapedOutputLineMap;
            if (eq) {
                eq = values.equals(((ScrapedOutputLineMap) o).values);
            }
            return eq;
        }

        public <T> T get(Request<T> request) {
            if (values.containsKey(request.name)) {
                return request.castStringOutput(values.get(request.name));
            } else {
                return null;
            }
        }

        @Override
        public int hashCode() {
            return values.hashCode();
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }
}
