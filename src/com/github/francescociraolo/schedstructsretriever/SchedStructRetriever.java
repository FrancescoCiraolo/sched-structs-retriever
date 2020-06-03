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

import com.github.francescociraolo.bcctrace.Request;
import com.github.francescociraolo.bcctrace.Trace;
import com.github.francescociraolo.scheddomains.LinkException;
import com.github.francescociraolo.scheddomains.SchedDomainManager;
import com.github.francescociraolo.scheddomains.SchedDomains;

import java.io.IOException;
import java.nio.file.Path;

import static com.github.francescociraolo.bcctrace.ValueType.*;

/**
 * Provides methods for retrieving the SchedDomains model.
 *
 * It works with a Trace to get domains struct variables and a Trace to obtain the groups struct data.
 *
 * @author Francesco Ciraolo
 */
public class SchedStructRetriever {

    /**
     * Return a {@link SchedDomains} object after retrieving the data using {@link Trace}.
     *
     * @param kernelPath the path of a linux kernel source.
     * @param bccPath the path to bcc directory.
     * @return a {@link SchedDomains} of sched domain topology in current runtime.
     * @throws IOException could be thrown from trace's script process mainly.
     * @throws LinkException could be thrown in case of inconsistent structures.
     */
    public static SchedDomains getSchedDomains(String kernelPath, String bccPath) throws IOException, LinkException {
        return getSchedDomains(new Trace(kernelPath, bccPath));
    }

    /**
     * Return a {@link SchedDomains} object after retrieving the data using {@link Trace}.
     *
     * @param traceTool a {@link Trace} used to retrieve the information.
     * @return a {@link SchedDomains} of sched domain topology in current runtime.
     * @throws IOException could be thrown from trace's script process mainly.
     * @throws LinkException could be thrown in case of inconsistent structures.
     */
    public static SchedDomains getSchedDomains(Trace traceTool) throws IOException, LinkException {

        var i = SchedDomains.cpuCount();
        var d = SchedDomains.domainsCount();

        var schedDomainManager = new SchedDomainManager(i);

        var cpu = new Request<>("cpu", D, "this_cpu");
        var name = new Request<>("name", S, "sd->name");
        var domainAddr = new Request<>("address", LU, "sd");
        var child = new Request<>("child", LU, "sd->child");
        var parent = new Request<>("parent", LU, "sd->parent");
        var span = new Request<>("span", MASK, "sd->span[0]");
        var group = new Request<>("groups", LU, "sd->groups");

        var tracing = traceTool.startTracing(
                Path.of("include/linux/sched/topology.h"),
                "load_balance(int this_cpu, struct rq *this_rq, struct sched_domain *sd)",
                cpu, name, domainAddr, child, parent, span, group);

        tracing
                .getStream()
                .distinct()
                .limit(d)
                .forEach(r -> schedDomainManager.addDomain(r.get(domainAddr),
                        r.get(cpu),
                        r.get(span),
                        r.get(group),
                        r.get(child),
                        r.get(parent)));
        tracing.close();

        var progression = new TwoSetProgression<>(schedDomainManager.getGroupsAddresses());

        var groupAddr = new Request<>("address", LU, "sg");
        var cpumask = new Request<>("cpumask", MASK, "sg->cpumask[0]");
        var next = new Request<>("next", LU, "sg->next");

        tracing = traceTool.startTracing(
                Path.of("kernel/sched/sched.h"),
                "group_balance_cpu(struct sched_group *sg)",
                groupAddr, cpumask, next);

        tracing
                .getStream()
                .takeWhile(r -> !progression.isResearchCompleted())
                .filter(r -> progression.update(r.get(groupAddr), r.get(next)))
                .forEach(r -> schedDomainManager.addGroup(r.get(groupAddr), r.get(cpumask), r.get(next)));

        tracing.close();

        return schedDomainManager.build();
    }
}
