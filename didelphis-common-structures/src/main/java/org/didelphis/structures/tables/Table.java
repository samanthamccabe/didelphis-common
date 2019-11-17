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

import org.didelphis.structures.Structure;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Interface {@code Table}
 * <p>
 * A general interface for two-dimensional matrix data structures
 *
 * @param <E> the type parameter
 *
 * @since 0.1.0
 */
public interface Table<E> extends Structure {

	/**
	 * Retrieves the value at the specified indices
	 *
	 * @param row index from where the value is to be read; must be >= 0
	 * @param col index from where the value is to be read; must be >= 0
	 *
	 * @return the value to be retrieved; returns null if no value is present
	 *
	 * @throws IndexOutOfBoundsException if either parameter is less than 0
	 */
	@NonNull
	E get(int row, int col);

	/**
	 * Sets a new value at the specified indices
	 *
	 * @param row index where the value is to be written; must be >= 0
	 * @param col index where the value is to be written; must be >= 0
	 * @param element the value to be written
	 *
	 * @return the previous value; null if no previous value was present
	 *
	 * @throws IndexOutOfBoundsException if either parameter is less than 0
	 */
	@NonNull
	E set(int row, int col, @NonNull E element);

	/**
	 * Returns the current number of rows
	 *
	 * @return current number of rows; guaranteed to be >= 0
	 */
	int rows();

	/**
	 * Returns the current number of columns
	 *
	 * @return current number of columns; guaranteed to be >= 0
	 */
	int columns();

	/**
	 * Returns a collection containing the contents of the specified row
	 *
	 * @param row the row whose contents are read; must be >= 0
	 *
	 * @return the contents of the specified row; cannot be null
	 *
	 * @throws IndexOutOfBoundsException if parameter is less than 0
	 */
	@NonNull
	List<E> getRow(int row);

	/**
	 * Returns a collection containing the contents of the specified column
	 *
	 * @param col the column whose contents are read; must be >= 0
	 *
	 * @return the contents of the specified column; cannot be null
	 *
	 * @throws IndexOutOfBoundsException if parameter is less than 0
	 */
	@NonNull
	List<E> getColumn(int col);

	/**
	 * Inserts data into the specified row and returns a collection of its
	 * previous contents
	 *
	 * @param row the row whose contents will overwritten; must be >= 0
	 *
	 * @return the contents of the specified row; cannot be null
	 *
	 * @throws IndexOutOfBoundsException if row is less than 0
	 */
	@NonNull
	List<E> setRow(int row, @NonNull List<E> data);

	/**
	 * Inserts data into the specified column and returns a collection of its
	 * previous contents
	 *
	 * @param col the column whose contents will be overwritten; must be >= 0
	 *
	 * @return the contents of the specified column; cannot be null
	 *
	 * @throws IndexOutOfBoundsException if column is less than 0
	 */
	@NonNull
	List<E> setColumn(int col, @NonNull List<E> data);

	/**
	 * Provides a {@link Stream} of all the elements in the table
	 *
	 * @return a {@link Stream} of all the elements in the table
	 */
	@NonNull
	Stream<E> stream();

	/**
	 * Applies a transformation to each element of the table; this is provided
	 * for cases where {@link Table#stream} cannot be used because the type
	 * {@code <E>} is immutable
	 *
	 * @param function the function to be applied to each element of the table
	 */
	void apply(@NonNull Function<E, E> function);

	/**
	 * Provides an {@link Iterator} over the rows of the table, each row
	 * represented by a {@link Collection} of elements
	 *
	 * @return an {@link Iterator} over the row of the table
	 */
	@NonNull
	Iterator<Collection<E>> rowIterator();

	/**
	 * Provides an {@link Iterator} over the columns of the table, each column
	 * represented by a {@link Collection} of elements
	 *
	 * @return an {@link Iterator} over the columns of the table
	 */
	@NonNull
	Iterator<Collection<E>> columnIterator();
}
