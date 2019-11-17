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

import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Interface {@code ColumnTable}
 *
 * A type of table with column headers
 *
 * @param <E>
 */
public interface ColumnTable<E> extends Table<E> {

	/**
	 * Checks if the table has the provided key
	 *
	 * @param key the column name to check for the presence of
	 *
	 * @return true iff the table contains the key
	 */
	boolean hasKey(@Nullable String key);

	/**
	 * Returns the column header names
	 *
	 * @return a list of the column header names; not {@code null}
	 */
	@NonNull
	List<String> getKeys();

	/**
	 * Retrieves a column by the header key
	 *
	 * @param key the header key of the column to return
	 *
	 * @return the contents of the specified column; cannot be {@code null}
	 */
	@Nullable
	List<E> getColumn(@NonNull String key);

	/**
	 * Set the header key for a particular column
	 *
	 * @param column the column index
	 * @param key the new column key; not {@code null}
	 *
	 * @return the previous key; not {@code null}
	 */
	@NonNull
	String setColumnKey(int column, @NonNull String key);

	/**
	 * Get the key for the specified column headers
	 *
	 * @param column the column index
	 *
	 * @return the column key for the specified index; not {@code null}
	 *
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater
	 *      than the number of columns
	 */
	@NonNull
	String getColumnKey(int column);
}
