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

package org.didelphis.structures.frames;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.structures.tables.Table;
import org.didelphis.structures.tables.TableColumn;
import org.didelphis.structures.tables.TableRow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@ToString (callSuper = true)
@EqualsAndHashCode
public class DataTable<E> implements Frame<E, FrameRow<E>, FrameColumn<E>> {

	private final List<String> keys;

	private final List<List<E>> data;

	public DataTable(@NonNull DataTable<E> table) {
		keys = table.keys;
		data = new ArrayList<>(table.data.size());
		for (List<E> datum : table.data) {
			data.add(new ArrayList<>(datum));
		}
	}

	public DataTable(@NonNull List<String> keys) {
		this.keys = keys;
		data = new ArrayList<>();
		// TODO????
	}

	public DataTable(
			@NonNull List<String> keys,
			@NonNull Collection<? extends Collection<E>> rowList
	) {
		this.keys = keys;
		data = new ArrayList<>(rowList.size());
		for (Collection<E> datum : rowList) {
			data.add(new ArrayList<>(datum));
		}
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
	public @Nullable List<E> getColumn(@NonNull String key) {
		return hasKey(key) ? getColumn(keys.indexOf(key)) : null;
	}

	@NonNull
	@Override
	public String setColumnKey(int column, @NonNull String key) {
		return keys.set(column, key);
	}

	@NonNull
	@Override
	public String getColumnKey(int column) {
		return keys.get(column);
	}

	@NonNull
	@Override
	public E get(int row, int col) {
		return null;
	}

	@NonNull
	@Override
	public E set(int row, int col, @NonNull E element) {
		return null;
	}

	@Override
	public int rows() {
		return 0;
	}

	@Override
	public int columns() {
		return 0;
	}

	@NonNull
	@Override
	public FrameRow<E> getRow(int row) {
		return null;
	}

	@NonNull
	@Override
	public FrameColumn<E> getColumn(int col) {
		return null;
	}

	@Override
	public @NonNull Stream<E> stream() {
		return null;
	}

	@Override
	public void apply(@NonNull Function<E, E> function) {

	}

	@Override
	public @NonNull Iterable<FrameRow<E>> rowIterator() {
		return null;
	}

	@Override
	public @NonNull Iterable<FrameColumn<E>> columnIterator() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void clear() {

	}

	private class DataRow<E> extends AbstractList<E> implements FrameRow<E> {

		@Override
		public E get(int index) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@NonNull
		@Override
		public E get(@Nullable String key) {
			return null;
		}

		@Override
		public boolean hasKey(@Nullable String key) {
			return false;
		}

		@Override
		public @NonNull List<String> getKeys() {
			return null;
		}

		@Override
		public boolean hasCommentField(@NonNull String key) {
			return false;
		}

		@Override
		public @NonNull String getComment(@NonNull String key) {
			return null;
		}

		@Override
		public int getIndex() {
			return 0;
		}

		@Override
		public @NotNull Table<E, TableRow<E>, TableColumn<E>> getTable() {
			return null;
		}
	}

	private class DataColumn<E> extends AbstractList<E>
			implements FrameColumn<E> {

		@Override
		public E get(int index) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public @NonNull String getKey() {
			return null;
		}

		@Override
		public int getIndex() {
			return 0;
		}

		@Override
		public @NotNull Table<E, TableRow<E>, TableColumn<E>> getTable() {
			return null;
		}
	}
}
