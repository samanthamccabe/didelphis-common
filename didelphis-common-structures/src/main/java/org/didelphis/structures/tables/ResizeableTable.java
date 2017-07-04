/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.structures.tables;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Samantha Fiona McCabe
 * Date: 5/25/17
 * <p>
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
	void insertRow(int row, @NotNull Collection<E> data);

	/**
	 * Insert a new column into the table; grows the table by one column
	 * @param col the column into which to insert the data; must be >= 0
	 * @param data the data to insert into the table
	 */
	void insertColumn(int col, @NotNull Collection<E> data);

	/**
	 * Removes and returns a collection containing the contents of the specified
	 * row; this shrinks the table by one row
	 * @param row the row whose contents are removed; must be >= 0
	 * @return a collection containing the contents of the specified row
	 */
	@NotNull
	Collection<E> removeRow(int row);

	/**
	 * Removes and returns a collection containing the contents of the specified
	 * columns; this shrinks the table by one column
	 * @param col the row whose contents are removed; must be >= 0
	 * @return a collection containing the contents of the specified column
	 */
	@NotNull
	Collection<E> removeColumn(int col);

}
