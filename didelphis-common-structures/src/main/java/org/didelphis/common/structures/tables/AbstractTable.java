/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.structures.tables;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/17/2016
 */
public abstract class AbstractTable<E> implements ResizeableTable<E> {

	@Deprecated
	protected static final DecimalFormat DECIMAL_FORMAT =
			new DecimalFormat(" 0.000;-0.000");

	private int rows;
	private int columns;

	protected AbstractTable(int rows, int columns) {
		if (rows < 0 || columns < 0) {
			throw new IndexOutOfBoundsException(
					"Dimensions of a table cannot be negative");
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
			throw new IndexOutOfBoundsException(
					"Row index: " + row + ", Rows: " + rows);
		}
	}

	protected void checkRow(int row) {
		if (row >= rows || row < 0) {
			throw new IndexOutOfBoundsException(
					"Row index: " + row + ", Rows: " + rows);
		}
	}

	protected void checkColEdge(int col) {
		if (col > columns || col < 0) {
			throw new IndexOutOfBoundsException(
					"Column index: " + col + ", Columns: " + columns);
		}
	}

	protected void checkCol(int col) {
		if (col >= columns || col < 0) {
			throw new IndexOutOfBoundsException(
					"Column index: " + col + ", Columns: " + columns);
		}
	}
	
	protected void checkRanges(int row, int col) {
		checkRow(row);
		checkCol(col);
	}

	protected void checkRowData(Collection<E> data) {
		if (data.size() != columns()) {
			throw new IllegalArgumentException("New row data is the wrong " +
					"size: " + data.size() + " but there are " + columns() +
					" columns.");
		}
	}

	protected void checkColumnData(Collection<E> data) {
		if (data.size() != rows()) {
			throw new IllegalArgumentException("New column data is the wrong " +
					"size: " + data.size() + " but there are " + rows() +
					" rows.");
		}
	}

	protected static void negativeCheck(int rows, int cols) {
		if (rows < 0 || cols < 0) {
			throw new IllegalArgumentException("expand/shrink operations must" +
					" not take negative arguments. r=" + rows + " c=" + cols);
		}
	}
}
