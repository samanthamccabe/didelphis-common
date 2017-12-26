
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Samantha Fiona McCabe
 * @date 8/23/2015
 */
public class DataTable<E>
		extends RectangularTable<E>
		implements ColumnTable<E> {

	private final List<String> keys;

	public DataTable(@NonNull DataTable<E> table) {
		super(table);
		keys = table.keys;
	}

	public DataTable(@NonNull List<String> keys) {
		super((E) null, 0, keys.size());
		this.keys = keys;
	}

	public DataTable(@NonNull List<String> keys, @NonNull Collection<? extends Collection<E>> rowList) {
		super(rowList, rowList.size(), keys.size());
		this.keys = keys;
	}

	@NonNull
	@Override
	public String toString() {
		return "DataTable{"+super.toString()+ '}';
	}

	@Override
	public boolean hasKey(String key) {
		return keys.contains(key);
	}

	@NonNull
	@Override
	public List<String> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	@Override
	public @Nullable List<E> getColumn(String key) {
		return hasKey(key) ? getColumn(keys.indexOf(key)) : null;
	}

	@NonNull
	@Override
	public
	String setColumnName(int column, String name) {
		return keys.set(column, name);
	}

	@NonNull
	@Override
	public
	String getColumnName(int column) {
		return keys.get(column);
	}

}
