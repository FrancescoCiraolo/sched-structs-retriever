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

import java.io.Serializable;

/**
 * Simple list of scheduling flags, just mirrors the headers' defines.
 *
 * @author Francesco Ciraolo
 */
@SuppressWarnings("unused")
public enum Flag implements Serializable {
    /** Do load balancing on this domain. */
    SD_LOAD_BALANCE(0x0001),
    /** Balance when about to become idle */
    SD_BALANCE_NEWIDLE(0x0002),
    /** Balance on exec */
    SD_BALANCE_EXEC(0x0004),
    /** Balance on fork, clone */
    SD_BALANCE_FORK(0x0008),
    /** Balance on wakeup */
    SD_BALANCE_WAKE(0x0010),
    /** Wake task to waking CPU */
    SD_WAKE_AFFINE(0x0020),
    /** Domain members have different CPU capacities */
    SD_ASYM_CPUCAPACITY(0x0040),
    /** Domain members share CPU capacity */
    SD_SHARE_CPUCAPACITY(0x0080),
    /** Domain members share power domain */
    SD_SHARE_POWERDOMAIN(0x0100),
    /** Domain members share CPU pkg resources */
    SD_SHARE_PKG_RESOURCES(0x0200),
    /** Only a single load balancing instance */
    SD_SERIALIZE(0x0400),
    /** Place busy groups earlier in the domain */
    SD_ASYM_PACKING(0x0800),
    /** Prefer to place tasks in a sibling domain */
    SD_PREFER_SIBLING(0x1000),
    /** sched_domains of this level overlap */
    SD_OVERLAP(0x2000),
    /** cross-node balancing */
    SD_NUMA(0x4000);

    /**
     * Binary value of flag.
     */
    public final int value;

    Flag(int value) {
        this.value = value;
    }

}