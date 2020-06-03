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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Progress tracker for research of unknown number of elements. Based on two sets, one with missing identifier and one with the found ones.
 * Initialized with starting identifiers and useful for circular list.
 *
 * @param <T> the identifier type of elements.
 * @author Francesco Ciraolo
 */
public class TwoSetProgression<T> {

    private final Set<T> found;
    private final Set<T> required;

    public TwoSetProgression(Collection<T> startingElements) {
        this.found = new HashSet<>();
        this.required = new HashSet<>(startingElements);
    }

    /**
     * Return true when the research is completed: the found set and the required one are equals.
     *
     * @return if the research is completed.
     */
    public synchronized boolean isResearchCompleted() {
        return found.equals(required);
    }

    /**
     * Update the progression state with a found identifier and the next identifier.
     *  @param found <code>T</code> type found identifier
     * @param required <code>T</code> type required identifier
     * @return true if this update is a new one
     */
    public synchronized boolean update(T found, T required) {
        var newOne = !this.found.contains(found);

        if (newOne) {
            this.required.add(required);
            this.required.add(found);
            this.found.add(found);
        }

        return newOne;
    }
}
