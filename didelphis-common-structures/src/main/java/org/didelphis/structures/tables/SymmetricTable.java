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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class {@code SymmetricTable}
 *
 * Note: as of version {@code 0.3.1} it is recommended that this class be used
 * judiciously. Testing has indicated substantial runtime costs in repeated
 * index lookups which is probably proportional to the size of the table. In
 * the future, a different implementation of this class will be adopted.
 *
 */
public class SymmetricTable<E> extends AbstractTable<E> {

	private final List<E> array;

	private SymmetricTable(int n) {
		super(n, n);
		array = new ArrayList<>(n + ((n * n - n) / 2));
	}

	public SymmetricTable(int n, @NonNull List<E> array) {
		super(n, n);
		int size = n + ((n * n - n) / 2);
		if (array.size() == size) {
			this.array = new ArrayList<>(array);
		} else {
			throw new IllegalArgumentException(
					"Array was provided with size " + array.size() + " but " +
							"must be " + size
			);
		}
	}

	public SymmetricTable(E defaultValue, int n) {
		this(n);
		int number = getIndex(n, n) - 1;
		for (int i = 0; i < number; i++) {
			array.add(defaultValue);
		}
	}

	protected SymmetricTable(@NonNull SymmetricTable<E> otherTable) {
		this(otherTable.rows());
		array.addAll(otherTable.array);
	}
	
	@NonNull
	@Override
	public E get(int row, int col) {
		return array.get(getIndex(col, row));
	}

	@NonNull
	@Override
	public E set(int row, int col, @NonNull E element) {
		int index = getIndex(col, row);
		return array.set(index, element);
	}

	@NonNull
	@Override
	public
	List<E> getRow(int row) {
		checkRow(row);
		int index = getIndex(row, 0);
		List<E> collection = new ArrayList<>(array.subList(index, index + row + 1));
		for (int i = row + 1; i < rows(); i++) {
			collection.add(get(row, i));
		}
		return collection;
	}

	@NonNull
	@Override
	public
	List<E> getColumn(int col) {
		return getRow(col);
	}

	@NonNull
	@Override
	public
	List<E> setRow(int row, @NonNull List<E> data) {
		checkRow(row);
		checkRowData(data);
		List<E> original = new ArrayList<>();
		int col = 0;
		for (E datum : data) {
			original.add(get(row, col));
			set(row, col, datum);
			col++;
		}
		return original;
	}

	@NonNull
	@Override
	public
	List<E> setColumn(int col, @NonNull List<E> data) {
		return setRow(col, data);
	}

	@NonNull
	@Override
	public Stream<E> stream() {
		return array.stream();
	}

	@Override
	public void apply(@NonNull Function<E, E> function) {
		for (int i = 0; i < array.size(); i++) {
			array.set(i, function.apply(array.get(i)));
		}
	}

	@NonNull
	@Override
	public Iterator<Collection<E>> rowIterator() {
		return new ColumnIterator<>(array, columns());
	}

	@NonNull
	@Override
	public Iterator<Collection<E>> columnIterator() {
		return new ColumnIterator<>(array, columns());
	}

	@NonNull
	@Deprecated
	@Override
	public String formattedTable() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j <= i; j++) {
				sb.append(get(j, i));
				if (j < i) {
					sb.append('\t');
				}
			}
			if (i < (rows() - 1)) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return SymmetricTable.class.hashCode() ^ array.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SymmetricTable)) return false;
		SymmetricTable<?> that = (SymmetricTable<?>) o;
		return Objects.equals(array, that.array);
	}

	@NonNull
	@Override
	public String toString() {
		return "SymmetricTable{"+ rows() + ": " + array + '}';
	}

	@Override
	public int size() {
		return rows()*columns();
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean clear() {
		boolean contentsRemoved = !isEmpty();
		array.clear();
		setRows(0);
		setColumns(0);
		return contentsRemoved;
	}

	@Override
	public void expand(int rows, int cols, E fillerValue) {
		negativeCheck(rows, cols);
		int max = Math.max(rows, cols);
		for (int i = 0; i < max; i++) {
			int size = i + rows() + 1;
			array.addAll(Collections.nCopies(size, fillerValue));
		}
		setRows(rows() + max);
		setColumns(columns() + max);
	}

	@Override
	public void shrink(int rows, int cols) {
		negativeCheck(rows, cols);
		int max = Math.max(rows, cols);
		int size = rows() - max;

		if (size < 0) {
			throw new IndexOutOfBoundsException("Unable to shrink table by " +
					max + " because the current size of the table is less " +
					"than this amount: " + rows());
		}

		int index = getIndex(size, 0);
		while (array.size() > index) {
			array.remove(index);
		}
		setRows(size);
		setColumns(size);
	}

	@Override
	public void insertRow(int row, @NonNull Collection<E> data) {
		checkRow(row);
		int size = columns() + 1;
		if (data.size() != size) {
			throw new IllegalArgumentException("New row data is the wrong " +
					"size: " + data.size() + " but there are " + size +
					" columns.");
		}
		List<E> list = new ArrayList<>(data);
		// Get index to start
		int start = getIndex(row, 0);
		// Insert r + 1 elements at index
		for (int i = 0; i < row + 1; i++) {
			array.add(start + i, list.remove(0));
		}
		// Find the next start index, increment by row; then insert an element
		int i = 1;
		while (!list.isEmpty()) {
			int index = getIndex(row + i, 0) + row;
			array.add(index, list.remove(0));
			i++;
		}
		setRows(size);
		setColumns(size);
	}

	@Override
	public void insertColumn(int col, @NonNull Collection<E> data) {
		insertRow(col, data);
	}

	@NonNull
	@Override
	public Collection<E> removeRow(int row) {
		checkRow(row);
		// Get index to start
		int start = getIndex(row, 0);
		// Remove r+1 elements
		Collection<E> list = IntStream.range(0, row + 1)
				.mapToObj(i -> array.remove(start))
				.collect(Collectors.toList());
		// While elements remain:
		int i = 1;
		while (list.size() < rows()) {
			int index = getIndex(row + i, 0) - list.size() + row;
			list.add(array.remove(index));
			i++;
		}

		setRows(rows() - 1);
		setColumns(columns() - 1);

		return list;
	}

	@NonNull
	@Override
	public Collection<E> removeColumn(int col) {
		return removeRow(col);
	}

	private static int getIndex(int i, int j) {
		return j >= i ? getRowStart(j) + i : getRowStart(i) + j;
	}

	private static int getRowStart(int row) {
		int sum = 0;
		for (int i = 0; i <= row; i++) {
			sum += i;
		}
		return sum;
	}

	private static final class ColumnIterator<E>
			implements Iterator<Collection<E>> {

		private final List<E> array;
		private final int columns;
		private int i;

		private ColumnIterator(List<E> list, int columns) {
			array = list;
			this.columns = columns;
		}

		@Override
		public boolean hasNext() {
			return i < columns;
		}

		@Override
		public Collection<E> next() {
			if (i >= columns) {
				throw new NoSuchElementException(
						"No element exists at column index " + i
				);			}
			Collection<E> list = IntStream.range(0, columns)
					.mapToObj(j -> array.get(getIndex(i, j)))
					.collect(Collectors.toList());
			i++;
			return list;
		}
	}
}
