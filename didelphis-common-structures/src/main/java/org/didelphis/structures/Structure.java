/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.structures;

/**
 * This interface defines a general contract for a set of methods used in many
 * classes. It can be used to indicate some behavior found in
 * {@link java.util.Collection} but is more general.
 *
 * Specifically, these are objects which can be said to have a size, and be
 * emptied of contents.
 *
 * Implementing classes are very likely to be {@link Iterable} but
 */
public interface Structure {

	/**
	 * Returns the size of the structure, based on the total number of values or
	 * unique key pairs
	 *
	 * @return the number of values in this structure
	 */
	int size();

	/**
	 * Tests if the structure is empty.
	 *
	 * @return true iff {@code size > 0}
	 */
	boolean isEmpty();

	/**
	 * Deletes all contents from the structure.
	 */
	void clear();
}
