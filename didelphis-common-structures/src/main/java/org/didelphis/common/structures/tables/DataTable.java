
/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.structures.tables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/23/2015
 */
public class DataTable<E>
		extends RectangularTable<E>
		implements ColumnTable<E> {

	private final List<String> keys;

	public DataTable(DataTable<E> table) {
		super(table);
		keys = table.keys;
	}

	public DataTable(List<String> keys) {
		super((E) null, 0, keys.size());
		this.keys = keys;
	}

	public DataTable(List<String> keys, Collection<? extends Collection<E>> rowList) {
		super(rowList, rowList.size(), keys.size());
		this.keys = keys;
	}

	@Override
	public String toString() {
		return "DataTable{"+super.toString()+"}";
	}

	@Override
	public boolean hasKey(String key) {
		return keys.contains(key);
	}

	@NotNull
	@Override
	public List<String> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	@Nullable
	@Override
	public List<E> getColumn(String key) {
		return hasKey(key) ? getColumn(keys.indexOf(key)) : null;
	}

	@Override
	public
	String setColumnName(int column, String name) {
		return keys.set(column, name);
	}

	@Override
	public
	String getColumnName(int column) {
		return keys.get(column);
	}

}
