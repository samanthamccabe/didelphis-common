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

import java.util.ArrayList;
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
public final class DataTable<E> implements ColumnTable<E> {
	
	private final Map<String, List<E>> columns;

	private final List<List<E>> rows;
	private final List<String> keys;

	private final int nRows;

	public DataTable(DataTable<E> table) {
		columns = new LinkedHashMap<>();
		table.columns.forEach((key,value) -> columns.put(key, new ArrayList<E>(value)));
		
		rows = new ArrayList<>(table.rows);
		keys = new ArrayList<>(table.keys);
		nRows = table.nRows;
	}
	
	public DataTable(Map<String, List<E>> map) {
		columns = new LinkedHashMap<>();
		keys = new ArrayList<>();
		rows = new ArrayList<>();

		int numberOfRows = 0;

		// Ensure we can 
		if (!map.isEmpty()) {
			Iterator<List<E>> iterator = map.values().iterator();
			numberOfRows = iterator.next().size();
			while (iterator.hasNext()) {
				int size = iterator.next().size();
				rangeCheck(numberOfRows, size);
			}
			columns.putAll(map);
			keys.addAll(map.keySet());

			for (int i = 0; i < numberOfRows; i++) {
				List<E> row = new ArrayList<>();
				for (String key : keys) {
					row.add(columns.get(key).get(i));
				}
				rows.add(row);
			}
		}
		nRows = numberOfRows;
	}

	@Override
	public int hashCode() {
		return Objects.hash(columns, rows, keys, nRows);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DataTable)) return false;
		DataTable<?> dataTable = (DataTable<?>) o;
		return (nRows == dataTable.nRows) &&
				Objects.equals(columns, dataTable.columns) &&
				Objects.equals(rows, dataTable.rows) &&
				Objects.equals(keys, dataTable.keys);
	}

	@Override
	public String toString() {
		return "DataTable{keys=" + keys + ", nRows=" + nRows + ", columns=" + columns + '}';
	}

	@Override
	public boolean hasKey(String key) {
		return keys.contains(key);
	}

	@Override
	public List<String> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	@Override
	public List<E> getColumn(String key) {
		if (hasKey(key)) {
			return Collections.unmodifiableList(columns.get(key));
		} else {
			return null;
		}
	}

	@Override
	public Map<String, E> getRowAsMap(int index) {
		Map<String, E> map = new LinkedHashMap<>();
		for (String key : keys) {
			map.put(key, columns.get(key).get(index));
		}
		return map;
	}

	@Override
	public List<E> getRow(int index) {
		checkRowIndex(index);
		return Collections.unmodifiableList(rows.get(index));
	}

	@Override
	public E get(int row, int col) {
		checkRowIndex(row);
		return columns.get(keys.get(col)).get(row);
	}

	@Override

	public void set(int row, int col, E element) {
		checkRowIndex(row);
		columns.get(keys.get(col)).set(row, element);
	}

	@Override
	public int getRows() {
		return nRows;
	}

	@Override
	public int getColumns() {
		return keys.size();
	}

	@Override
	public String formattedTable() {
		StringBuilder sb = new StringBuilder();

		Iterator<String> keyItr = keys.iterator();
		while (keyItr.hasNext()) {
			sb.append(keyItr.next());
			if (keyItr.hasNext()) sb.append('\t');
		}
		sb.append('\n');

		for (List<E> row : rows) {
			Iterator<E> rowItr = row.iterator();
			while (rowItr.hasNext()) {
				sb.append(rowItr.next());
				if (rowItr.hasNext()) sb.append('\t');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

	@Override
	public Iterator<List<E>> iterator() {
		return Collections.unmodifiableCollection(rows).iterator();
	}

	private static void rangeCheck(int n, int i) {
		if (n != i) {
			throw new IllegalArgumentException(
					"DataTable cannot be instantiated using a Map whose " +
							"values are Lists of inconsistent length");
		}
	}

	private void checkRowIndex(int index) {
		if (nRows <= index) {
			throw new IndexOutOfBoundsException("Attempting to access row " +
					index + " of a table with only " + nRows + " rows");
		}
	}
}
