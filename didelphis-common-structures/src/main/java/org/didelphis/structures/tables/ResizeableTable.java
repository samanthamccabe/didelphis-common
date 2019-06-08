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

package org.didelphis.structures.tables;

import lombok.NonNull;

import java.util.Collection;

/**
 *
 * A general interface for two-dimensional matrix data structures
 * @param <E> the type parameter
 */
public interface ResizeableTable<E> extends Table<E> {

	/**
	 * Expands the dimensions of the table by the amounts provided.
	 * @param rows number of rows by which to expand the table; must be >= 0
	 * @param cols number of columns by which to expand the table; must be >= 0
	 * @param fillerValue
	 */
	void expand(int rows, int cols, E fillerValue);

	/**
	 * Reduces the dimensions of the table by the amounts provided. Any values
	 * outside the table's new dimensions are discarded.
	 * @param rows number of rows by which to shrink the table; must be >= 0
	 * @param cols number of columns by which to shrink the table; must be >= 0
	 */
	void shrink(int rows, int cols);

	/**
	 * Insert a new row into the table; grows the table by one row
	 * @param row the row into which to insert the data; must be >= 0
	 * @param data the data to insert into the table
	 */
	void insertRow(int row, @NonNull Collection<E> data);

	/**
	 * Insert a new column into the table; grows the table by one column
	 * @param col the column into which to insert the data; must be >= 0
	 * @param data the data to insert into the table
	 */
	void insertColumn(int col, @NonNull Collection<E> data);

	/**
	 * Removes and returns a collection containing the contents of the specified
	 * row; this shrinks the table by one row
	 * @param row the row whose contents are removed; must be >= 0
	 * @return a collection containing the contents of the specified row
	 */
	@NonNull
	Collection<E> removeRow(int row);

	/**
	 * Removes and returns a collection containing the contents of the specified
	 * columns; this shrinks the table by one column
	 * @param col the row whose contents are removed; must be >= 0
	 * @return a collection containing the contents of the specified column
	 */
	@NonNull
	Collection<E> removeColumn(int col);

}
