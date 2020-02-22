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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.utilities.Templates;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class {@code RectangularTable}
 *
 * @param <E>
 *
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class RectangularTable<E>
		extends AbstractTable<E, TableRow<E>, TableColumn<E>> {

	private final List<E> array;

	private RectangularTable(int row, int col) {
		super(row, col);
		array = new ArrayList<>(row * col);
	}

	protected RectangularTable(
			@NonNull Iterable<? extends Iterable<E>> rowList, int row, int col
	) {
		this(row, col);
		for (Iterable<E> rowElements : rowList) {
			for (E element : rowElements) {
				array.add(element);
			}
		}
	}

	public RectangularTable(@Nullable E defaultValue, int row, int col) {
		this(row, col);
		for (int i = 0; i < row * col; i++) {
			array.add(defaultValue);
		}
	}

	public RectangularTable(@NonNull RectangularTable<E> table) {
		this(table.rows(), table.columns());
		array.addAll(table.array);
	}

	/**
	 * Retrieve the element at the specified location
	 *
	 * @param row the index for row
	 * @param col the index for column
	 *
	 * @return the object stored at these coordinates
	 *
	 * @throws IndexOutOfBoundsException if either row or column is negative
	 */
	@NonNull
	@Override
	public E get(int row, int col) {
		checkRanges(row, col);
		int index = getIndex(row, col);
		return array.get(index);
	}

	/**
	 * Put an element into the specified location in the Table
	 *
	 * @param row the index for row
	 * @param col the index for column
	 * @param element the object to place at the specified coordinates
	 *
	 * @return the previous value
	 *
	 * @throws IndexOutOfBoundsException if either row or column is negative
	 */
	@NonNull
	@Override
	public E set(int row, int col, @NonNull E element) {
		checkRanges(row, col);
		int index = getIndex(row, col);
		return array.set(index, element);
	}

	@NonNull
	@Override
	public TableRow<E> getRow(int row) {
		checkRow(row);
		int startIndex = getIndex(row, 0);
		int endIndex = getIndex(row, columns());
		List<E> list = array.subList(startIndex, endIndex);
		return new Row<>(row, list, this);
	}

	@NonNull
	@Override
	public TableColumn<E> getColumn(int col) {
		checkCol(col);
		List<E> collect = IntStream.range(0, rows())
				.mapToObj(i -> get(i, col))
				.collect(Collectors.toList());
		return new Column<>(col, collect, this);
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
	public Iterable<TableRow<E>> rowIterator() {
		return () -> new RowIterator<>(this, array, rows(), columns());
	}

	@NonNull
	@Override
	public Iterable<TableColumn<E>> columnIterator() {
		return () -> new ColumnIterator<>(this, array, rows(), columns());
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
	public void clear() {
		array.clear();
		setRows(0);
		setColumns(0);
	}

	@Override
	public void expand(int rows, int cols, E fillerValue) {
		negativeCheck(rows, cols);

		List<E> newRow = Collections.nCopies(columns(), fillerValue);
		for (int i = 0; i < rows; i++) {
			insertRow(rows(), new Row<>(i, newRow, this));
		}

		List<E> newColumn = Collections.nCopies(rows(), fillerValue);
		for (int i = 0; i < cols; i++) {
			insertColumn(columns(), new Column<>(i, newColumn, this));
		}
	}

	@Override
	public void shrink(int rows, int cols) {
		negativeCheck(rows, cols);

		if (rows > rows() || cols > columns()) {
			String message = Templates.create()
					.add("Unable to shrink table by {}, {} because",
							"the current size of the table is less than",
							"this amount: {}, {}")
					.with(rows, cols, rows(), columns())
					.build();
			throw new IndexOutOfBoundsException(message);
		}

		for (int i = 0; i < rows; i++) {
			removeRow(rows() - 1);
		}
		for (int i = 0; i < cols; i++) {
			removeColumn(columns() - 1);
		}
	}

	@Override
	public void insertRow(int row, @NonNull List<E> data) {
		checkRowEdge(row);
		checkRowData(data);
		array.addAll(getIndex(row, 0), data);
		setRows(rows() + 1);
	}

	@Override
	public void insertColumn(int col, @NonNull List<E> data) {
		checkColEdge(col);
		checkColumnData(data);
		int index = getIndex(0, col);
		for (E e : data) {
			array.add(index, e);
			index += columns() + 1;
		}
		setColumns(columns() + 1);
	}

	@NonNull
	@Override
	public TableRow<E> removeRow(int row) {
		checkRow(row);
		int index = getIndex(row, 0);
		List<E> list = new ArrayList<>();
		while (list.size() < columns()) {
			list.add(array.remove(index));
		}
		setRows(rows() - 1);
		return new Row<>(row, list, this);
	}

	@NonNull
	@Override
	public TableColumn<E> removeColumn(int col) {
		checkCol(col);
		int index = getIndex(0, col);
		List<E> list = new ArrayList<>();
		while (list.size() < rows()) {
			list.add(array.remove(index));
			index += columns() - 1;
		}
		setColumns(columns() - 1);
		return new Column<E>(col, list, this);
	}

	/**
	 * Computes and returns the absolute index of the internal array based on
	 * the provided coordinates.
	 *
	 * @param col the column position
	 * @param row the row position
	 *
	 * @return the absolute index of the internal array based on the provided
	 *      coordinates.
	 */
	private int getIndex(int row, int col) {
		return getIndex(row, col, columns());
	}

	private static int getIndex(int row, int col, int columns) {
		return col + row * columns;
	}

	@ToString
	@EqualsAndHashCode
	private static final class RowIterator<E>
			implements Iterator<TableRow<E>> {

		private final Table<E, TableRow<E>, TableColumn<E>> table;
		private final List<E>  list;

		private final int rows;
		private final int columns;
		private       int i;

		private RowIterator(Table<E, TableRow<E>, TableColumn<E>> table, List<E> list, int rows, int columns) {
			i = 0;
			this.table = table;
			this.list = list;
			this.rows = rows;
			this.columns = columns;
		}

		@Override
		public boolean hasNext() {
			return i < rows;
		}

		@Override
		public TableRow<E> next() {
			if (i >= rows) {
				throw new NoSuchElementException(
						"No element exists at row index " + i
				);
			}
			int index1 = getIndex(i, 0, columns);
			int index2 = getIndex(i + 1, 0, columns);
			i++;
			List<E> subList = list.subList(index1, index2);
			return new Row<>(i, subList, table);
		}
	}

	@ToString
	@EqualsAndHashCode
	private static final class ColumnIterator<E>
			implements Iterator<TableColumn<E>> {

		private final Table<E, TableRow<E>, TableColumn<E>> table;
		private final List<E>  list;

		private final int rows;
		private final int columns;
		private       int i;

		private ColumnIterator(
				Table<E, TableRow<E>, TableColumn<E>> table,
				List<E> list,
				int rows,
				int columns
		) {
			i = 0;
			this.table = table;
			this.list = list;
			this.rows = rows;
			this.columns = columns;
		}

		@Override
		public boolean hasNext() {
			return i < columns;
		}

		@Override
		public TableColumn<E> next() {
			if (i >= columns) {
				throw new NoSuchElementException(
						"No element exists at column index " + i
				);
			}
			List<E> list = IntStream.range(0, rows)
					.mapToObj(r -> this.list.get(getIndex(r, i, columns)))
					.collect(Collectors.toList());
			i++;
			return new Column<>(i, list, table);
		}
	}
}
