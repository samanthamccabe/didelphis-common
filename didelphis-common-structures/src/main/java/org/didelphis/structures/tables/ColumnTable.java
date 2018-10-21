/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.structures.tables;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @date 8/23/2015
 */
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
