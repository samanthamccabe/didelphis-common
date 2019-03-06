
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
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
