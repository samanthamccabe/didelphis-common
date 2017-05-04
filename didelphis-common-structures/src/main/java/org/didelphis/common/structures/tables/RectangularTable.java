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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @param <E>
 *
 * @author Samantha Fiona Morrigan McCabe
 */
public final class RectangularTable<E> extends AbstractTable<E> {

	private final List<E> array;

	private RectangularTable(int row, int col) {
		super(row, col);
		array = new ArrayList<>(row * col);
	}

	public RectangularTable(List<List<E>> values, int row, int col) {
		this(row, col);
		for (Iterable<E> iterable : values) {
			for (E element : iterable) {
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
		this(table.getRows(), table.getColumns());
		array.addAll(table.array);
	}

	/**
	 * Retrieve the element at the specified location
	 *
	 * @param col the index for column
	 * @param row the index for row
	 *
	 * @return the object stored at these coordinates
	 */
	@Override
	public E get(int col, int row) {
		rangeCheck(col, getColumns());
		rangeCheck(row, getRows());
		int index = getIndex(col, row);
		return array.get(index);
	}

	/**
	 * Put an element into the specified location in the Table
	 *  @param col the index for column
	 * @param row the index for row
	 * @param element the object to place at the specified coordinates
	 */
	@Override
	public void set(int col, int row, E element) {
		rangeCheck(col, getColumns());
		rangeCheck(row, getRows());
		int index = getIndex(col, row);
		array.set(index, element);
	}

	@Deprecated
	@Override
	public String formattedTable() {
		StringBuilder sb = new StringBuilder(array.size() * 8);

		int i = 1;
		for (E e : array) {
			if (e instanceof Double) {
				sb.append(DECIMAL_FORMAT.format(e));
				if (i % getColumns() == 0) {
					sb.append('\n');
				} else {
					sb.append("  ");
				}
				i++;
			}
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(array, getColumns(), getRows());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RectangularTable)) return false;
		RectangularTable<?> that = (RectangularTable<?>) o;
		return Objects.equals(array, that.array)
		       && getRows() == that.getRows()
		       && getColumns() == that.getColumns();
	}

	@Override
	public String toString() {
		return "RectangularTable{" +
				"numberRows=" + getRows() +
				", numberColumns=" + getColumns() +
				", array=" + array +
				'}';
	}

	/**
	 * Computes and returns the absolute index of the internal array based on
	 * the provided coordinates.
	 *
	 * @param col the column position
	 * @param row the row position
	 *
	 * @return the absolute index of the internal array based on the provided
	 * coordinates.
	 */
	private int getIndex(int col, int row) {
		return col + row * getColumns();
	}
}
