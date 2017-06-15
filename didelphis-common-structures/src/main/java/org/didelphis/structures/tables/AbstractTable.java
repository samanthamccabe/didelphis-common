/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.structures.tables;

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
