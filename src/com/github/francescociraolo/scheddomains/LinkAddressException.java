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
 * An element required for link operations is missing.
 *
 * @author Francesco Ciraolo
 */
public class LinkAddressException extends LinkException {

    private final long address;

    public LinkAddressException(long address) {
        super(String.format("Element at address %d isn't described", address));
        this.address = address;
    }

    public long getAddress() {
        return address;
    }
}
