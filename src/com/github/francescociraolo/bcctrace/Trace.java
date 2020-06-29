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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Trace's basic Java Wrapper.
 * <p>
 * It implements, currently, only a method which generalize an invocation of trace's script for retrieving information
 * structured from each line.
 * {@link #startTracing(TraceStreamHandler, Path, String, Request[]) startTracing} requires the relative
 * {@link Path Path} to the header of interest, the signature of requested method and a number of
 * {@link Request Request}s which defines the information needed and the type of each one.
 *
 * @author Francesco Ciraolo
 */
public class Trace {

    private final String kernelSourcePath;
    private final String traceBin;

    /**
     * The constructor requires a valid kernelSourcePath and the bccPath, if bcc isn't installed
     * in a default path.
     *
     * @param kernelSourcePath the path to a valid linux kernel source
     * @param bccPath the path, optional, of a bcc installation directory
     */
    public Trace(String kernelSourcePath, String bccPath) {
        this.kernelSourcePath = kernelSourcePath;

        if (!Files.exists(Path.of(kernelSourcePath))) throw new RuntimeException("Missing kernel source");

        Path trace = null;
        if (bccPath != null) trace = Path.of(bccPath, "tools", "trace");
        if (trace == null || !Files.exists(trace)) trace = Path.of("/usr", "sbin", "trace-bpfcc");
        if (!Files.exists(trace)) trace = Path.of("/usr", "sbin", "bpftrace");
        if (!Files.exists(trace)) throw new RuntimeException("Missing trace bin");

        this.traceBin = trace.toString();
    }

    /**
     * Start a tracing process, managing the results with the passed {@link TraceStreamHandler}.
     *
     * @param handler the handler to which delegate the stream processing
     * @param relativeHeaderPath the path to a required header, relative to linux source directory
     * @param signature the method signature, useful for sync with requests variables' name
     * @param requests the required variables, their name and their type
     * @throws IOException if the process launch cause some problem
     */
    public void startTracing(TraceStreamHandler handler,
                             Path relativeHeaderPath,
                             String signature,
                             Request<?>... requests) throws IOException {

        var header = Path.of(kernelSourcePath).resolve(relativeHeaderPath);

        if (!Files.exists(header)) throw new RuntimeException("Missing header file");

        var format = new StringBuilder();
        var variables = new StringBuilder();

        for (var request : requests) {
            format.append(String.format("[%s=%s]", request.getName(), request.getFormat()));
            if (variables.length() > 0) variables.append(", ");
            variables.append(request.getVar());
        }

        var command = String.format(
                "%s -I %s '%s \"%s\", %s'",
                traceBin,
                header.toString(),
                signature,
                format.toString(),
                variables.toString());

        var process = new ProcessBuilder("sudo", "bash", "-c", command).start();

        var shutdownHook = new Thread(process::destroy);

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        var scanner = new Scanner(process.getInputStream());

        var stream = Stream
                .generate(() -> {
                    synchronized (scanner) {
                        return scanner.nextLine();
                    }
                })
                .map(Trace::extractResults)
                .flatMap(Optional::stream);

        handler.streamOperation(stream);

        process.destroy();

        Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    /**
     * Pattern to retrieve the results from trace lines.
     */
    private static final Pattern PATTERN0 = Pattern.compile("\\[([^=]*)=([^]]*)]");

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
            T res = null;
            var name = request.getName();
            if (values.containsKey(name))
                res = request.castStringOutput(values.get(name));
            return res;
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
