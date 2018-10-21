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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.utilities.Templates;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Abstract Class {@code AbstractTable}
 *
 * @date 4/17/2016
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
public abstract class AbstractTable<E> implements ResizeableTable<E> {

	@Deprecated protected static final DecimalFormat DECIMAL_FORMAT
			= new DecimalFormat(" 0.000;-0.000");

	private int rows;
	private int columns;

	protected AbstractTable(int rows, int columns) {
		if (rows < 0 || columns < 0) {
			String message = Templates.create()
					.add("Cannot create a table with negative dimensions!")
					.add("Parameters provided: row {} / col {}")
					.with(rows, columns)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
		this.rows = rows;
		this.columns = columns;
	}

	protected void setRows(int size) {
		rows = size;
	}

	protected void setColumns(int size) {
		columns = size;
	}

	@Override
	public int rows() {
		return rows;
	}

	@Override
	public int columns() {
		return columns;
	}

	protected void checkRowEdge(int row) {
		if (row > rows || row < 0) {
			String message = Templates.create()
					.add("Row parameter ({}) is out of bounds while trying to",
							"expand table.")
					.with(row)
					.add("Currently there are {} rows")
					.with(rows)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
	}

	protected void checkRow(int row) {
		if (row >= rows || row < 0) {
			String message = Templates.create()
					.add("Row parameter ({}) is out of bounds!")
					.with(row)
					.add("Currently there are {} rows")
					.with(rows)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
	}

	protected void checkColEdge(int col) {
		if (col > columns || col < 0) {
			String message = Templates.create()
					.add("Column parameter ({}) is out of bounds while trying " 
							+ "to expand table.")
					.with(col)
					.add("Currently there are {} columns")
					.with(columns)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
	}

	protected void checkCol(int col) {
		if (col >= columns || col < 0) {
			String message = Templates.create()
					.add("Column parameter ({}) is out of bounds!")
					.with(col)
					.add("Currently there are {} columns")
					.with(columns)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
	}

	protected void checkRanges(int row, int col) {
		checkRow(row);
		checkCol(col);
	}

	protected void checkRowData(@NonNull Collection<E> data) {
		if (data.size() != columns()) {
			String message = Templates.create()
					.add("New row is the wrong size!")
					.add("Has {} columns but needs {}.")
					.with(data.size(), columns())
					.data(data)
					.build();
			throw new IllegalArgumentException(message);
		}
	}

	protected void checkColumnData(@NonNull Collection<E> data) {
		if (data.size() != rows()) {
			String message = Templates.create()
					.add("New column is the wrong size!")
					.add("Has {} rows but needs {}.")
					.with(data.size(), rows())
					.data(data)
					.build();
			throw new IllegalArgumentException(message);
		}
	}

	protected static void negativeCheck(int rows, int cols) {
		if (rows < 0 || cols < 0) {
			String message = Templates.create()
					.add("Operations to expand or shrink a table cannot")
					.add("have negative arguments!")
					.add("The arguments provided were row: {} and col: {}")
					.with(rows, cols)
					.build();
			throw new IllegalArgumentException(message);
		}
	}
}
