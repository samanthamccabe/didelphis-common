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

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/17/2016
 */
public abstract class AbstractTable<E> implements Table<E> {

	protected static final DecimalFormat DECIMAL_FORMAT =
			new DecimalFormat(" 0.000;-0.000");

	private final int rows;
	private final int columns;

	protected AbstractTable(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getColumns() {
		return columns;
	}

	protected static void rangeCheck(int index, int size) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(
					"Index: " + index + ", Size: " + size
			);
		}
	}

}
