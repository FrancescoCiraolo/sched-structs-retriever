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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link SchedDomains}' creation controller. It keeps all the object and the list of their required objects.
 *
 * @author Francesco Ciraolo
 */
public class SchedDomainManager {

    private final CPU[] cpus;

    private final HashMap<Long, Domain> domainsAddress;
    private final HashMap<Long, Group> groupsAddress;

    private final HashMap<Domain, Long> domainsParent;
    private final HashMap<Domain, Long> domainsChild;
    private final HashMap<Domain, Long> domainGroup;

    private final HashMap<Group, Long> nextGroup;

    public SchedDomainManager(int cpuCount) {
        cpus = new CPU[cpuCount];
        for (int i = 0; i < cpuCount; i++) cpus[i] = new CPU(i);
        domainsAddress = new HashMap<>();
        groupsAddress = new HashMap<>();
        domainsParent = new HashMap<>();
        domainsChild = new HashMap<>();
        domainGroup = new HashMap<>();
        nextGroup = new HashMap<>();
    }

    /**
     * Allows to add a domain with the variables obtainable from
     * <code>load_balance(int this_cpu, struct rq *this_rq, struct sched_domain *sd)</code>.
     *
     * @param address the memory address of current struct, for linking purpose.
     * @param ownerID the id of cpu owning this domain.
     * @param spanIDs the id of cpus involved in this domain.
     * @param groupAddress the memory address of the first group in the circular list, for linking purpose.
     * @param childAddress the memory address of child domain, for linking purpose.
     * @param parentAddress the memory address of parent domain, for linking purpose.
     */
    public void addDomain(long address,
                          int ownerID,
                          int[] spanIDs,
                          long groupAddress,
                          long childAddress,
                          long parentAddress) {

        var owner = this.cpus[ownerID];
        var span = new CPU[spanIDs.length];
        for (int i = 0; i < span.length; i++) span[i] = cpus[spanIDs[i]];
        var domain = new Domain(owner, span);

        domainsAddress.put(address, domain);
        if (childAddress != 0) domainsChild.put(domain, childAddress);
        if (parentAddress != 0) domainsParent.put(domain, parentAddress);
        domainGroup.put(domain, groupAddress);
    }

    /**
     * Allows to add a group with the variables obtainable from
     * <code>load_balance(int this_cpu, struct rq *this_rq, struct sched_domain *sd)</code>.
     *
     * @param address the memory address of current struct, for linking purpose.
     * @param membersID the ids of the member cpus.
     * @param nextGroupAddress the memory address of the next group in the circular list, for linking purpose.
     */
    public void addGroup(long address, int[] membersID, long nextGroupAddress) {
        var span = new CPU[membersID.length];
        for (int i = 0; i < span.length; i++) span[i] = cpus[membersID[i]];
        var group = new Group(span);
        groupsAddress.put(address, group);
        nextGroup.put(group, nextGroupAddress);
    }

    /**
     * Create a {@link SchedDomains} from the stored objects and links.
     *
     * @return Called after the addition of all required objects, returns a valid {@link SchedDomains}.
     * @throws LinkException if there are missing objects or invalid parent-child link.
     */
    public SchedDomains build() throws LinkException {
        var groups = new HashMap<Long, HashSet<Group>>();
        var remaining = groupsAddress.keySet();

        for (var groupAddress : remaining) {
            if (!groups.containsKey(groupAddress)) {
                var groupsSet = new HashSet<Group>();
                var addressesSet = new HashSet<Long>();

                long address = groupAddress;

                do {
                    var group = groupsAddress.get(address);
                    if (group == null) throw new LinkAddressException(address);
                    groupsSet.add(group);
                    addressesSet.add(address);
                    address = nextGroup.get(group);
                } while (address != groupAddress);

                for (var a : addressesSet) groups.put(a, groupsSet);
            }
        }

        for (var e : domainGroup.entrySet()) {
            var group = groups.get(e.getValue());
            if (group == null) throw new LinkAddressException(e.getValue());
            e.getKey().setGroups(group);
        }

        if (domainsParent.size() != domainsChild.size())
            throw new UnmatchedParentChildSizeException(domainsParent.size(), domainsChild.size());

        for (var e : domainsParent.entrySet()) {
            var domain = e.getKey();
            var parent = domainsAddress.get(e.getValue());
            if (parent == null) throw new LinkAddressException(e.getValue());

            if (!domainsChild.containsKey(parent) || domainsAddress.get(domainsChild.get(parent)) != domain) {
                throw new UnmatchedParentChildException();
            }

            parent.setChild(domain);
            domain.setParent(parent);
        }

        domainsAddress
                .values()
                .forEach(Domain::verifyAndComplete);

        domainsAddress
                .values()
                .stream()
                .collect(Collectors.groupingBy(Domain::getOwner))
                .forEach(CPU::setDomains);

        var cpus = new CPU[this.cpus.length];
        System.arraycopy(this.cpus, 0, cpus, 0, cpus.length);
        return new SchedDomains(cpus);
    }

    /**
     * Allow to get the addresses of all required groups until now.
     *
     * @return the memory addresses of the required groups.
     */
    public Collection<Long> getGroupsAddresses() {
        return Set.copyOf(domainGroup.values());
    }

}
