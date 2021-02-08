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
import java.util.Set;

import static com.github.francescociraolo.bcctrace.ValueType.*;

/**
 * Provides methods for retrieving the SchedDomains model.
 * <p>
 * It works with a Trace to get domains struct variables and a Trace to obtain the groups struct data.
 *
 * @author Francesco Ciraolo
 */
public class SchedStructRetriever {

    /**
     *
     * Return a {@link SchedDomains} object after retrieving the data using {@link Trace}.
     *
     * @param kernelPath the path of a linux kernel source.
     * @param bccPath    the path to bcc directory.
     * @return a {@link SchedDomains} of sched domain topology in current runtime.
     * @throws IOException   could be thrown from trace's script process mainly.
     * @throws LinkException could be thrown in case of inconsistent structures.
     */
    public static SchedDomains getSchedDomains(String kernelPath, String bccPath) throws IOException, LinkException {
        return getSchedDomains(Path.of(kernelPath), new Trace(bccPath));
    }

    /**
     * Return a {@link SchedDomains} object after retrieving the data using {@link Trace}.
     *
     * @param traceTool a {@link Trace} used to retrieve the information.
     * @return a {@link SchedDomains} of sched domain topology in current runtime.
     * @throws IOException   could be thrown from trace's script process mainly.
     * @throws LinkException could be thrown in case of inconsistent structures.
     */
    public static SchedDomains getSchedDomains(Path kernelPath, Trace traceTool) throws IOException, LinkException {

        var cpuCount = SchedDomains.cpuCount();
        var domainsCount = SchedDomains.domainsCount();

        var schedDomainManager = new SchedDomainManager(cpuCount);

        //The following requests are required to build the domains and their hierarchies.
        var cpu = Request.getSimpleRequest("cpu", D, "this_cpu");
        var name = Request.getSimpleRequest("name", S, "sd->name");
        var domainAddr = Request.getSimpleRequest("address", LU, "sd");
        var child = Request.getSimpleRequest("child", LU, "sd->child");
        var parent = Request.getSimpleRequest("parent", LU, "sd->parent");
        var span = Request.getSimpleRequest("span", MASK, "sd->span[0]");
        var group = Request.getSimpleRequest("groups", LU, "sd->groups");

        traceTool
                /*
                    Following the lambda implementation of a TraceStreamHandler;
                    in particular its goal is to read each different lines, limited to domainsCount,
                    and pass their scraped Domains to schedDomainManager.
                 */
                .startTracing(stream -> stream
                                .distinct()
                                .limit(domainsCount)
                                .forEach(line ->
                                        schedDomainManager.addDomain(
                                                line.get(domainAddr),
                                                line.get(cpu),
                                                line.get(span),
                                                line.get(group),
                                                line.get(child),
                                                line.get(parent))),
                        Set.of(kernelPath.resolve("include/linux/sched/topology.h")),
                        "load_balance(int this_cpu, struct rq *this_rq, struct sched_domain *sd)",
                        cpu, name, domainAddr, child, parent, span, group);

        var progression = new TwoSetProgression<>(schedDomainManager.getGroupsAddresses());

        //The following requests are required to build the groups and to fill them.
        var groupAddr = Request.getSimpleRequest("address", LU, "sg");
        var cpumask = Request.getSimpleRequest("cpumask", MASK, "sg->cpumask[0]");
        var next = Request.getSimpleRequest("next", LU, "sg->next");

        traceTool
                /*
                    Following the lambda implementation of a TraceStreamHandler;
                    in particular its goal is to read each different lines, until all groups are read,
                    and pass their scraped Group object to schedDomainManager.
                 */
                .startTracing(
                        stream -> stream
                                .takeWhile(line -> !progression.isResearchCompleted())
                                .filter(line -> progression.update(line.get(groupAddr), line.get(next)))
                                .forEach(line ->
                                        schedDomainManager.addGroup(
                                                line.get(groupAddr),
                                                line.get(cpumask),
                                                line.get(next))),
                        Set.of(kernelPath.resolve("kernel/sched/sched.h")),
                        "group_balance_cpu(struct sched_group *sg)",
                        groupAddr, cpumask, next);

        return schedDomainManager.build();
    }
}
