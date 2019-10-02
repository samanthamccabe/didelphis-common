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

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Abstract Class {@code AbstractTable}
 *
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
		int size = data.size();
		if (size != columns()) {
			String message = Templates.create()
					.add("New row is the wrong size!")
					.add("Has {} columns but needs {}.")
					.with(size, columns())
					.data(data)
					.build();
			throw new IllegalArgumentException(message);
		}
	}

	protected void checkColumnData(@NonNull Collection<E> data) {
		int size = data.size();
		if (size != rows()) {
			String message = Templates.create()
					.add("New column is the wrong size!")
					.add("Has {} rows but needs {}.")
					.with(size, rows())
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
