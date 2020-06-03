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

/**
 * Kernel's scheduling group model.
 *
 * @author Francesco Ciraolo
 */
public class Group {

    /**
     * Group's members as <code>CPU</code> array
     */
    private final CPU[] members;

    Group(CPU[] members) {
        this.members = new CPU[members.length];
        System.arraycopy(members, 0, this.members, 0, members.length);
    }

    /**
     * @return a copy of members' array.
     */
    public CPU[] getMembers() {
        var res = new CPU[members.length];
        System.arraycopy(members, 0, res, 0, members.length);
        return res;
    }
}
