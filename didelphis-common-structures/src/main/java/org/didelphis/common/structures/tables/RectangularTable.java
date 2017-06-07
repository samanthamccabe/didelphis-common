/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.didelphis.common.structures.tables;

import org.didelphis.common.structures.contracts.Delegating;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @param <E>
 * @author Samantha Fiona Morrigan McCabe
 */
public class RectangularTable<E>
		extends AbstractTable<E>
		implements Delegating<List<E>> {

	private final List<E> array;

	private RectangularTable(int row, int col) {
		super(row, col);
		array = new ArrayList<>(row * col);
	}

	public RectangularTable(Iterable<? extends Iterable<E>> rowList, int row, int col) {
		this(row, col);
		for (Iterable<E> rowElements : rowList) {
			for (E element : rowElements) {
				array.add(element);
			}
		}
	}

	public RectangularTable(E defaultValue, int row, int col) {
		this(row, col);
		for (int i = 0; i < row * col; i++) {
			array.add(defaultValue);
		}
	}

	public RectangularTable(RectangularTable<E> table) {
		this(table.rows(), table.columns());
		array.addAll(table.array);
	}

	/**
	 * Retrieve the element at the specified location
	 * @param row the index for row
	 * @param col the index for column
	 * @return the object stored at these coordinates
	 */
	@Override
	public E get(int row, int col) {
		checkRanges(row, col);
		int index = getIndex(row, col);
		return array.get(index);
	}

	/**
	 * Put an element into the specified location in the Table
	 * @param row the index for row
	 * @param col the index for column
	 * @param element the object to place at the specified coordinates
	 */
	@Override
	public E set(int row, int col, E element) {
		checkRanges(row, col);
		int index = getIndex(row, col);
		return array.set(index, element);
	}

	@NotNull
	@Override
	public
	List<E> getRow(int row) {
		checkRow(row);
		int startIndex = getIndex(row, 0);
		int endIndex   = getIndex(row, columns());
		return array.subList(startIndex, endIndex);
	}

	@Override
	public
	List<E> getColumn(int col) {
		checkCol(col);
		return IntStream.range(0, rows())
				.mapToObj((int i) -> get(i, col))
				.collect(Collectors.toList());
	}

	@NotNull
	@Override
	public
	List<E> setRow(int row, List<E> data) {
		checkRow(row);
		checkRowData(data);
		List<E> oldEntries = new ArrayList<>(getRow(row));
		int i = 0;
		for (E e : data) {
			set(row, i, e);
			i++;
		}
		return oldEntries;
	}

	@Override
	public
	List<E> setColumn(int col, List<E> data) {
		checkCol(col);
		checkColumnData(data);
		List<E> oldEntries = getColumn(col);
		int i = 0;
		for (E e : data) {
			set(i, col, e);
			i++;
		}
		return oldEntries;
	}

	@Deprecated
	@Override
	public String formattedTable() {
		StringBuilder sb = new StringBuilder(array.size() * 8);
		int i = 1;
		for (E e : array) {
			if (e instanceof Double) {
				sb.append(DECIMAL_FORMAT.format(e));
				sb.append((i % columns() == 0) ? "\n" : "  ");
				i++;
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(array, columns(), rows());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RectangularTable)) return false;
		RectangularTable<?> that = (RectangularTable<?>) o;
		return Objects.equals(array, that.array) && rows() == that.rows() &&
				columns() == that.columns();
	}

	@Override
	public String toString() {
		return "RectangularTable{numberRows=" + rows() + ", numberColumns=" +
				columns() + ", array=" + array + '}';
	}

	@Override
	public int size() {
		return array.size();
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean clear() {
		boolean clear = !isEmpty();
		array.clear();
		setRows(0);
		setColumns(0);
		return clear;
	}

	@Override
	public void expand(int rows, int cols) {
		negativeCheck(rows, cols);

		List<E> newRow = Collections.nCopies(columns(), null);
		for (int i = 0; i < rows; i++) {
			insertRow(rows(), newRow);
		}

		List<E> newColumn = Collections.nCopies(rows(), null);
		for (int i = 0; i < cols; i++) {
			insertColumn(columns(), newColumn);
		}
	}

	@Override
	public void shrink(int rows, int cols) {
		negativeCheck(rows, cols);

		if (rows > rows() || cols > columns()) {
			throw new IndexOutOfBoundsException("Unable to shrink table by " +
					rows + ", "+ cols  + " because the current size of the " +
					"table is less than this amount: " +
					rows() + ", " + columns());
		}

		for (int i = 0; i < rows; i++) {
			removeRow(rows() - 1);
		}
		for (int i = 0; i < cols; i++) {
			removeColumn(columns() - 1);
		}
	}

	@Override
	public void insertRow(int row, @NotNull Collection<E> data) {
		checkRowEdge(row);
		checkRowData(data);
		array.addAll(getIndex(row, 0), data);
		setRows(rows() + 1);
	}

	@Override
	public void insertColumn(int col, @NotNull Collection<E> data) {
		checkColEdge(col);
		checkColumnData(data);
		int index = getIndex(0, col);
		for (E e : data) {
			array.add(index, e);
			index += columns() + 1;
		}
		setColumns(columns() + 1);
	}

	@NotNull
	@Override
	public Collection<E> removeRow(int row) {
		checkRow(row);
		int index = getIndex(row, 0);
		Collection<E> list = new ArrayList<>();
		while (list.size() < columns()) {
			list.add(array.remove(index));
		}
		setRows(rows() - 1);
		return list;
	}

	@NotNull
	@Override
	public Collection<E> removeColumn(int col) {
		checkCol(col);
		int index = getIndex(0, col);
		Collection<E> list = new ArrayList<>();
		while (list.size() < rows()) {
			list.add(array.remove(index));
			index += columns() - 1;
		}
		setColumns(columns() - 1);
		return list;
	}

	@Override
	public List<E> getDelegate() {
		return Collections.unmodifiableList(array);
	}

	/**
	 * Computes and returns the absolute index of the internal array based on
	 * the provided coordinates.
	 * @param col the column position
	 * @param row the row position
	 * @return the absolute index of the internal array based on the provided
	 * coordinates.
	 */
	private int getIndex(int row, int col) {
		return col + row * columns();
	}
}
