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

package org.didelphis.common.structures.tables;

import org.didelphis.common.structures.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 11/30/2014
 * <p>
 * A general interface for two-dimensional matrix data structures
 * @param <E> the type parameter
 */
public interface Table<E> extends Structure {

	/**
	 * Retrieves the value at the specified indices
	 * @param row index from where the value is to be read; must be >= 0
	 * @param col index from where the value is to be read; must be >= 0
	 * @return the value to be retrieved; returns null if no value is present
	 */
	@Nullable
	E get(int row, int col);

	/**
	 * Sets a new value at the specified indices
	 * @param row index where the value is to be written; must be >= 0
	 * @param col index where the value is to be written; must be >= 0
	 * @param element the value to be written
	 * @return the previous value; null if no previous value was present
	 */
	@Nullable
	E set(int row, int col, @Nullable E element);

	/**
	 * Returns the current number of rows
	 * @return current number of rows; guaranteed to be >= 0
	 */
	int rows();

	/**
	 * Returns the current number of columns
	 * @return current number of columns; guaranteed to be >= 0
	 */
	int columns();

	/**
	 * Returns a collection containing the contents of the specified row
	 * @param row the row whose contents are read; must be >= 0
	 * @return the contents of the specified row; cannot be null
	 */
	@NotNull
	List<E> getRow(int row);

	/**
	 * Returns a collection containing the contents of the specified column
	 * @param col the column whose contents are read; must be >= 0
	 * @return the contents of the specified column; cannot be null
	 */
	List<E> getColumn(int col);

	/**
	 * Inserts data into the specified row and returns a collection of its 
	 * previous contents
	 * @param row the row whose contents will overwritten; must be >= 0
	 * @return the contents of the specified row; cannot be null
	 */
	@NotNull
	List<E> setRow(int row, List<E> data);

	/**
	 * Inserts data into the specified column and returns a collection of its
	 * previous contents
	 * @param col the column whose contents will be overwritten; must be >= 0
	 * @return the contents of the specified column; cannot be null
	 */
	List<E> setColumn(int col, List<E> data);
	
	/**
	 * Formatted table string.
	 * @return the string
	 */
	@Deprecated
	String formattedTable();
}
