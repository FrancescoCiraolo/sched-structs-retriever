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

/**
 * Model of flags' set, with simple operations.
 *
 * @author Francesco Ciraolo
 */
public class FlagsValue {

    /**
     * The value of flags set
     */
    private final int value;

    public FlagsValue(int value) {
        this.value = value;
    }

    /**
     * Create a <code>FlagValue</code> with this flags plus the ones passed as args.
     *
     * @param flags collection of sets to be added.
     * @return the representation of the created set.
     */
    public FlagsValue addAll(Collection<Flag> flags) {
        var tempV = value;
        for (var f : flags) tempV |= f.value;
        return new FlagsValue(tempV);
    }

    /**
     * Export a binary representation of the set.
     *
     * @return integer of binary representation.
     */
    public int getValue() {
        return value;
    }

    /**
     * Create a <code>FlagValue</code> with this flags but the ones passed as args.
     *
     * @param flags collection of sets to be added.
     * @return the representation of the created set.
     */
    public FlagsValue removeAll(Collection<Flag> flags) {
        var tempV = value;
        for (var f : flags) tempV &= ~f.value;
        return new FlagsValue(tempV);
    }

}
