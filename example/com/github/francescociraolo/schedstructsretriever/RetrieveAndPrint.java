package com.github.francescociraolo.schedstructsretriever;

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

import com.github.francescociraolo.scheddomains.CPU;
import com.github.francescociraolo.scheddomains.Group;
import com.github.francescociraolo.scheddomains.LinkException;

import java.io.IOException;

/**
 * Simple executable class for test and example purpose.
 *
 * Passing a linux kernel source directory path as 1st parameter and, optionally, a bcc directory path as 2nd one,
 * a {@link com.github.francescociraolo.scheddomains.SchedDomains} is created and printed as stdio.
 *
 * Requires root privileges.
 *
 * @author Francesco Ciraolo
 */
public class RetrieveAndPrint {

    public static String simpleStats(CPU[] cpus) {
        var builder = new StringBuilder();

        for (var c : cpus) {
            builder.append(String.format("CPU%d\n", c.getID()));
            for (var d : c.getDomains()) {
                builder.append(String.format("\tDomain%d {", d.getID()));
                builder.append("Groups=[");
                var groups = d.getGroups();
                var groups1 = groups.toArray(new Group[0]);
//                Arrays.sort(groups, new SimpleGroupComparator());
                for (int gI = 0; gI < groups1.length; gI++) {
                    var g = groups1[gI];
                    builder.append("[");
                    var members = g.getMembers();
                    for (int i = 0; i < members.length; i++) {
                        if (i > 0) builder.append(", ");
                        builder.append(String.format("CPU%d", members[i].getID()));
                    }
                    if (gI < groups1.length - 1) builder.append("], ");
                }
                builder.append("]}\n");
            }
        }

        return builder.toString().replaceAll("\t", "  ");
    }

    public static void main(String[] args) throws IOException, LinkException {
        var process = Runtime.getRuntime().exec("id -un");
        var username = new String(process.getInputStream().readAllBytes()).strip();

        if (!username.equals("root")) {
            System.err.println("Execution requires root");
            System.exit(1);
        }

        if (args.length == 0) {
            System.err.println("Need a valid linux kernel source path as 1st parameter");
            System.exit(1);
        }

        var bccPath = args.length > 1 ? args[1]  : "/usr/share/bcc";
        var domains = SchedStructRetriever.getSchedDomains(args[0], bccPath);
        var simpleStats = simpleStats(domains.getCpus());
        System.out.println(simpleStats);
    }
}
