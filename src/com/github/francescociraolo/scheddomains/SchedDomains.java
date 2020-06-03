package com.github.francescociraolo.scheddomains;

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
import java.util.Scanner;


/**
 * SchedDomains' model: keeps a <code>CPU[]</code> and offers some useful IO methods.
 *
 * @author Francesco Ciraolo
 */
public class SchedDomains {

    private final static Path SCHED_DOMAIN_PATH;

    static {
        SCHED_DOMAIN_PATH = Path.of("/proc/sys/kernel/sched_domain");
    }

    /**
     * Compute the cpu count from directories in /proc/sys/kernel/sched_domain.
     *
     * @return the computed cpu count.
     * @throws IOException as usual.
     */
    public static int cpuCount() throws IOException {
        return (int) Files.list(SCHED_DOMAIN_PATH).count();
    }

    /**
     * Compute the domains count from sub directories of all /proc/sys/kernel/sched_domain/cpu*.
     *
     * @return the computed domains count.
     * @throws IOException as usual.
     */
    public static int domainsCount() throws IOException {
        return (int) Files
                .list(SCHED_DOMAIN_PATH)
                .mapToLong(f -> {
                    try {
                        return Files.list(f).count();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    /**
     * Return the /proc/sys/kernel/sched_domain/cpu*\/domain* path of passed {@link Domain}.
     *
     * @param domain the {@link Domain} which path is required.
     * @return the {@link Path} of the passed domain.
     */
    public static Path getDomainPath(Domain domain) {
        return SCHED_DOMAIN_PATH
                .resolve(String.format("cpu%d", domain.getOwner().getID()))
                .resolve(String.format("domain%d", domain.getID()));
    }

    /**
     * Return the /proc/sys/kernel/sched_domain/cpu*\/domain*\/flags value of passed {@link Domain}.
     *
     * It throws a {@link RuntimeException} instead of the usual {@link IOException} for stream requirement.
     *
     * @param domain the {@link Domain} which flags value is required.
     * @return the {@link FlagsValue} of the passed domain.
     */
    @SuppressWarnings("unused")
    public static FlagsValue getFlagsValue(Domain domain) {
        try (var scanner = new Scanner(Files.newInputStream(getDomainPath(domain).resolve("flags")))) {
            return new FlagsValue(scanner.nextInt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */

    private final CPU[] cpus;

    SchedDomains(CPU[] cpus) {
        this.cpus = cpus;
    }

    /**
     * Return the {@link CPU} which is identified by the passed int.
     *
     * @param id the identifier of required {@link CPU}.
     * @return the {@link CPU} required.
     */
    @SuppressWarnings("unused")
    public CPU getCPU(int id) {
        return cpus[id];
    }

    /**
     * Return a copy of the {@link CPU}s array.
     *
     * @return a {@link CPU}[] copy of the private <code>cpus[]</code>.
     */
    public CPU[] getCpus() {
        var cpus = new CPU[this.cpus.length];
        System.arraycopy(this.cpus, 0, cpus, 0, this.cpus.length);
        return cpus;
    }
}
