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


public interface ColumnTable<E> extends Table<E> {

	/**
	 * Checks if the table has the provided key
	 * @param key the column name to check for the presence of
	 * @return true iff the table contains the key
	 */
	boolean hasKey(@Nullable String key);

	/**
	 * Returns the
	 * @return
	 */
	@NonNull
	List<String> getKeys();

	/**
	 *
	 * @param key
	 * @return
	 */
	@Nullable
	List<E> getColumn(@Nullable String key);

	/**
	 *
	 * @param column
	 * @param name
	 * @return
	 */
	@NonNull
	String setColumnName(int column, String name);

	/**
	 *
	 * @param column
	 * @return
	 * 
	 * @throws IndexOutOfBoundsException if 
	 */
	@NonNull
	String getColumnName(int column);
}
