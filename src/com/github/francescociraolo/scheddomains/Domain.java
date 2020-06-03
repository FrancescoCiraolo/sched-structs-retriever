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
import java.util.Comparator;
import java.util.Set;

/**
 * Kernel's scheduling domain model. Not each kernel struct's fields is mapped, just some ones useful by now.
 *
 * @author Francesco Ciraolo
 */
public class Domain {

    /**
     * The id of current domain, as the integer in /proc/sys/kernel/sched_domains/cpu*\/domain.
     */
    private int id;

    /**
     * The cpu on which current domain is built.
     */
    private final CPU owner;
    /**
     * The span of cpus in this domain.
     */
    private final CPU[] span;

    /**
     * Set of groups of current groups.
     */
    private Set<Group> groups;

    private Domain child;
    private Domain parent;

    private boolean valid;

    public Domain(CPU owner, CPU[] span) {
        this.id = -1;

        this.owner = owner;
        this.span = span;

        this.groups = null;
        this.child = this.parent = null;

        this.valid = false;
    }

    //Methods for initialization and package verification

    /**
     * @return state of invalidation of this Domain
     */
    boolean isInvalid() {
        return !valid;
    }

    /**
     * @param groups setter of domains groups' set
     */
    void setGroups(Collection<Group> groups) {
        this.groups = Set.copyOf(groups);
    }

    /**
     * Set child of this <code>Domain</code>
     *
     * @param domain child reference
     */
    void setChild(Domain domain) {
        this.child = domain;
    }

    /**
     * Set parent of this <code>Domain</code>
     *
     * @param domain parent reference
     */
    void setParent(Domain domain) {
        this.parent = domain;
    }

    /**
     * Inner method for check validation and complete initialization.
     */
    void verifyAndComplete() {
        if (!valid) {
            if (child != null) {
                child.verifyAndComplete();
                this.id = child.getID() + 1;
            } else {
                this.id = 0;
            }

            if (groups == null)
                throw new RuntimeException(String.format(
                        "CPU%d's Domain%d isn't correctly initialized: missing groups",
                        owner.getID(),
                        id));

            valid = true;
        }
    }

    //Methods for api usage

    /**
     * Return child of this <code>Domain</code>
     *
     * @return reference of this <code>Domain</code>'s child
     */
    public Domain getChild() {
        return child;
    }

    /**
     * Getter of <code>Domain</code>'s id
     *
     * @return the id of this <code>Domain</code>
     */
    public int getID() {
        return id;
    }

    /**
     * Return an immutable set of <code>Domain</code>'s groups
     *
     * @return the set of groups
     */
    public Set<Group> getGroups() {
        return groups;
    }

    /**
     * Getter this <code>Domain</code>'s owner <code>CPU</code>
     *
     * @return owner cpu's reference
     */
    public CPU getOwner() {
        return owner;
    }

    /**
     * Return parent of this <code>Domain</code>
     *
     * @return reference of this <code>Domain</code>'s parent
     */
    public Domain getParent() {
        return parent;
    }

    /**
     * Get a copy of CPU span array
     *
     * @return the array containing references of CPU in span
     */
    public CPU[] getSpan() {
        var res = new CPU[span.length];
        System.arraycopy(span, 0, res, 0, res.length);
        return res;
    }

    /**
     * Return a simple comparator based on cpu indices and then on domain indices
     * @return a <code>Comparator</code> instance
     */
    public static Comparator<Domain> getDefaultComparator() {
        return new DefaultComparator();
    }

    private static class DefaultComparator implements Comparator<Domain> {

        @Override
        public int compare(Domain o1, Domain o2) {
            if (o1.isInvalid() || o2.isInvalid())
                throw new RuntimeException("Trying to compare invalid domains");

            var r = o1.getOwner().compareTo(o2.getOwner());

            if (r == 0)
                r = o1.getID() - o2.getID();

            return r;
        }
    }
}
