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

import com.github.francescociraolo.schedstructsretriever.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Trace's basic Java Wrapper.
 *
 * It implements, currently, only a method which generalize an invocation of trace's script for retrieving information
 * structured from each line.
 * {@link #startTracing(Path, String, Request[]) startTracing} requires the relative {@link Path Path} to the
 * header of interest, the signature of requested method and a number of {@link Request Request}s which defines the
 * information needed and the type of each one.
 * The returned {@link Tracing Tracing} object provide the stream of information retrieved from the output lines of
 * trace script.
 *
 * @author Francesco Ciraolo
 */
public class Trace {

    private final String kernelSourcePath;
    private final String traceBin;

    /**
     *
     *
     * @param kernelSourcePath
     * @param bccPath
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

    public Tracing startTracing(Path relativeHeaderPath,
                                String signature,
                                Request<?>... requests) throws IOException {

        var header = Path.of(kernelSourcePath).resolve(relativeHeaderPath);

        if (!Files.exists(header)) {
            throw new RuntimeException("Missing header file");
        }

        var request = Arrays.stream(requests)
                .map(Request::getTracePair)
                .reduce(Request::mergeRequestPairs)
                .orElse(new Pair<>("", ""));

        var command = String.format(
                "%s -I %s '%s \"%s\", %s'",
                traceBin,
                header.toString(),
                signature,
                request.getFirst(),
                request.getSecond());

        System.out.println(command);

        var process = new ProcessBuilder("sudo", "bash", "-c", command).start();
        new Thread(() -> {
            var errorStream = process.getErrorStream();
            try {
                while (process.isAlive()) {
                    if (errorStream.available() > 0) {
                        System.out.println(new String(errorStream.readAllBytes()));
                    }
                }
            } catch (IOException ignored) {}
        })
                .start();
        return new Tracing(process);
    }

}
