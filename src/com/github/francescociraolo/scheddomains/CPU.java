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

import java.util.Arrays;
import java.util.Collection;

/**
 * SchedDomain's <code>CPU</code> model.
 *
 * The <code>SchedDomain</code>'s instances are comparable by the id.
 *
 * @author Francesco Ciraolo
 */
public class CPU implements Comparable<CPU> {

    /**
     * ID of the current CPU.
     */
    private final int id;

    /**
     * Array of the {@link Domain}s's built on this <code>CPU</code>.
     */
    private Domain[] domains;

    /**
     *
     * @param id the id of the current CPU.
     */
    CPU(int id) {
        this.id = id;
    }

    void setDomains(Collection<Domain> domains) {
        this.domains = domains.toArray(Domain[]::new);
        Arrays.sort(this.domains, Domain.getDefaultComparator());
    }

    /**
     *
     * @return an array of the {@link Domain}s's built on this <code>CPU</code>.
     */
    public Domain[] getDomains() {
        var domains = new Domain[this.domains.length];
        System.arraycopy(this.domains, 0, domains, 0, domains.length);
        return domains;
    }

    public int getID() {
        return id;
    }

    @Override
    public int compareTo(CPU o) {
        return id - o.getID();
    }
}
