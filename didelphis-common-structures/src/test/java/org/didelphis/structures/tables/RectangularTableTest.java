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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;


class RectangularTableTest {

	private static RectangularTable<String> table;

	@BeforeEach
	void init() {
		List<List<String>> data = new ArrayList<>();
		data.add(asList( "0", "1", "2"));
		data.add(asList( "3", "4", "5"));
		data.add(asList( "6", "7", "8"));
		data.add(asList( "9", "A", "B"));

		table = new RectangularTable<>(data, 4, 3);
	}

	@Test
	void constructor1() {
		Table<String> table1 = new RectangularTable<>("X", 2, 2);
		assertEquals("X", table1.get(0, 0));
		assertEquals("X", table1.get(0, 1));
		assertEquals("X", table1.get(1, 0));
		assertEquals("X", table1.get(1, 1));
	}

	@Test
	void constructor1ThrowsIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> new RectangularTable<>("", -1, -1));
	}

	@Test
	void constructor2() {
		List<List<String>> data = new ArrayList<>();
		List<String> row1 = new ArrayList<>();
		Collections.addAll(row1, "0", "1", "2");
		List<String> row2 = new ArrayList<>();
		Collections.addAll(row2, "3", "4", "5");
		Collections.addAll(data, row1, row2);

		Table<String> table1 = new RectangularTable<>(data, 2, 3);
		assertEquals("0", table1.get(0, 0));
		assertEquals("1", table1.get(0, 1));
		assertEquals("2", table1.get(0, 2));
		assertEquals("3", table1.get(1, 0));
		assertEquals("4", table1.get(1, 1));
		assertEquals("5", table1.get(1, 2));
	}

	@Test
	void constructor3() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		assertEquals(table, table1);
	}

	@Test
	void get() {
		assertEquals("0", table.get(0, 0));
		assertEquals("1", table.get(0, 1));
		assertEquals("2", table.get(0, 2));
		assertEquals("3", table.get(1, 0));
		assertEquals("4", table.get(1, 1));
		assertEquals("5", table.get(1, 2));
		assertEquals("6", table.get(2, 0));
		assertEquals("7", table.get(2, 1));
		assertEquals("9", table.get(3, 0));
		assertEquals("A", table.get(3, 1));
		assertEquals("B", table.get(3, 2));
	}

	@Test
	void get_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.get(-1,0));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.get(5,0));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.get(2,-1));
	}

	@Test
	void set() {
		Table<String> table1 = new RectangularTable<>(table);
		table1.set(3, 0, "X");
		table1.set(3, 1, "Y");
		table1.set(3, 2, "Z");

		assertEquals("X", table1.get(3, 0));
		assertEquals("Y", table1.get(3, 1));
		assertEquals("Z", table1.get(3, 2));
	}

	@Test
	void setThrowIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.set(-1,0, ""));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.set(5,0, ""));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.set(2,-1, ""));
	}

	@Test
	void equals() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		assertEquals(table1, table);
	}

	@Test
	void getNumberRows() {
		assertEquals(4, table.rows());
	}

	@Test
	void getNumberColumns() {
		assertEquals(3, table.columns());
	}

	@Test
	void testHashCode() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		RectangularTable<String> table2 = new RectangularTable<>(table);
		table2.set(3, 0, "X");
		table2.set(3, 1, "Y");
		table2.set(3, 2, "Z");

		assertEquals(table.hashCode(), table1.hashCode());
		assertNotEquals(table.hashCode(), table2.hashCode());
	}

	@Test
	void testToString() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		RectangularTable<String> table2 = new RectangularTable<>(table);
		table2.set(3, 0, "X");
		table2.set(3, 1, "Y");
		table2.set(3, 2, "Z");

		assertEquals(table.toString(), table1.toString());
		assertNotEquals(table.toString(), table2.toString());
	}

	@Test
	void getRow() {
		assertEquals(asList("0", "1", "2"), table.getRow(0));
		assertEquals(asList("3", "4", "5"), table.getRow(1));
		assertEquals(asList("6", "7", "8"), table.getRow(2));
		assertEquals(asList("9", "A", "B"), table.getRow(3));
	}

	@Test
	void getRowThrowsIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getRow(-1));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getRow(5));
	}

	@Test
	void getColumn() {
		assertEquals(asList("0", "3", "6", "9"), table.getColumn(0));
		assertEquals(asList("1", "4", "7", "A"), table.getColumn(1));
		assertEquals(asList("2", "5", "8", "B"), table.getColumn(2));
	}

	@Test
	void getColumnThrowsIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getColumn(-1));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getColumn(5));
	}

	@Test
	void setRow() {
		List<String> row0 = asList("!", "%", "W");
		List<String> row1 = asList("@", "^", "X");
		List<String> row2 = asList("#", "&", "Y");
		List<String> row3 = asList("$", "*", "Z");

		assertEquals(asList("0", "1", "2"), table.setRow(0, row0));
		assertEquals(asList("3", "4", "5"), table.setRow(1, row1));
		assertEquals(asList("6", "7", "8"), table.setRow(2, row2));
		assertEquals(asList("9", "A", "B"), table.setRow(3, row3));

		assertEquals(row0, table.getRow(0)) ;
		assertEquals(row1, table.getRow(1)) ;
		assertEquals(row2, table.getRow(2)) ;
		assertEquals(row3, table.getRow(3)) ;
	}

	@Test
	void setRowThrowsIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
		             () -> table.setRow(0, new ArrayList<>()));
	}

	@Test
	void setColumn() {
		List<String> column0 = asList("X", "Y", "Z", "W");
		List<String> column1 = asList("!", "@", "#", "$");
		List<String> column2 = asList("%", "^", "&", "*");

		assertEquals(asList("0", "3", "6", "9"), table.setColumn(0, column0));
		assertEquals(asList("1", "4", "7", "A"), table.setColumn(1, column1));
		assertEquals(asList("2", "5", "8", "B"), table.setColumn(2, column2));

		assertEquals(column0, table.getColumn(0));
		assertEquals(column1, table.getColumn(1));
		assertEquals(column2, table.getColumn(2));
	}

	@Test
	void setColumnThrowsIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
		             () -> table.setColumn(0, new ArrayList<>()));
	}

	@Test
	void size() {
		assertEquals(12, table.size());
	}

	@Test
	void isEmpty() {
		assertFalse(table.isEmpty());
	}

	@Test
	void clear() {
		table.clear();
		assertTrue(table.isEmpty());
		assertEquals(0, table.rows());
		assertEquals(0, table.columns());
	}

	@Test
	void expand2By1() {
		table.expand(2, 1, "");
		assertEquals(6, table.rows());
		assertEquals(4, table.columns());

		assertEquals(asList("0", "1", "2", ""), table.getRow(0));
		assertEquals(asList("3", "4", "5", ""), table.getRow(1));
		assertEquals(asList("", "", "", ""), table.getRow(5));

		assertEquals(asList("0", "3", "6", "9", "", ""), table.getColumn(0));
	}

	@Test
	void expand0By0() {
		table.expand(0, 0,"");
		assertEquals(4, table.rows());
		assertEquals(3, table.columns());
	}

	@Test
	void expandThrowsIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.expand(-1, 0, ""));
		assertThrows(IllegalArgumentException.class,
				() -> table.expand(0, -1, ""));
	}

	@Test
	void shrink1By1() {
		table.shrink(1, 1);
		assertEquals(3, table.rows());
		assertEquals(2, table.columns());

		assertEquals(asList("0", "1"), table.getRow(0));
		assertEquals(asList("3", "4"), table.getRow(1));
		assertEquals(asList("6", "7"), table.getRow(2));

		assertEquals(asList("0", "3", "6"), table.getColumn(0));
		assertEquals(asList("1", "4", "7"), table.getColumn(1));
	}

	@Test
	void shrink0By0() {
		table.shrink(0, 0);
		assertEquals(4, table.rows());
		assertEquals(3, table.columns());
	}

	@Test
	void shrinkThrowsIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.shrink(-1, 0));
		assertThrows(IllegalArgumentException.class,
				() -> table.shrink(0, -1));
	}

	@Test
	void insertRowIndex0() {
		List<String> data = asList("X", "Y", "Z");
		table.insertRow(0, data);

		assertEquals(data, table.getRow(0));
		assertEquals(asList("0", "1", "2"), table.getRow(1));
		assertEquals(5, table.rows());
	}

	@Test
	void insertRowIndex1() {
		List<String> data = asList("X", "Y", "Z");
		table.insertRow(1, data);

		assertEquals(asList("0", "1", "2"), table.getRow(0));
		assertEquals(data, table.getRow(1));
		assertEquals(5, table.rows());
	}

	@Test
	void insertRowThrowsIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(-1, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(7, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(5, new ArrayList<>()));
	}

	@Test
	void insertRowThrowsIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.insertRow(0, new ArrayList<>()));
	}

	@Test
	void insertColumnIndex0() {
		List<String> data = asList("W", "X", "Y", "Z");
		table.insertColumn(0, data);

		assertEquals(4, table.columns());
		assertEquals(data, table.getColumn(0));
		assertEquals(asList("0", "3", "6", "9"), table.getColumn(1));
	}

	@Test
	void insertColumnIndex1() {
		List<String> data = asList("W", "X", "Y", "Z");
		table.insertColumn(1, data);
		assertEquals(4, table.columns());
		assertEquals(asList("0", "3", "6", "9"), table.getColumn(0));
		assertEquals(data, table.getColumn(1));
		assertEquals(asList("1", "4", "7", "A"), table.getColumn(2));
	}

	@Test
	void insertColumnThrowsIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(-1, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(7, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(5, new ArrayList<>()));
	}

	@Test
	void removeRow() {
		assertEquals(asList("0", "1", "2"), table.removeRow(0));
		assertEquals(3, table.rows());

		assertEquals(asList("6", "7", "8"), table.removeRow(1));
		assertEquals(2, table.rows());

		assertEquals(asList("3", "4", "5"), table.getRow(0));
	}

	@Test
	void removeColumnIndex0() {
		assertEquals(asList("0", "3", "6", "9"), table.removeColumn(0));
		assertEquals(2, table.columns());
	}

	@Test
	void removeColumnIndex1() {
		assertEquals(asList("1", "4", "7", "A"), table.removeColumn(1));
		assertEquals(2, table.columns());
	}

	@Test
	void removeColumnIndex2() {
		assertEquals(asList("2", "5", "8", "B"), table.removeColumn(2));
		assertEquals(2, table.columns());
	}

	@Test
	void testColumnIterator() {
		Iterator<Collection<String>> iterator = table.columnIterator();
		List<Collection<String>> received = new ArrayList<>();
		while (iterator.hasNext()) {
			received.add(iterator.next());
		}
		assertEquals(asList("0", "3", "6", "9"), received.get(0));
		assertEquals(asList("1", "4", "7", "A"), received.get(1));
		assertEquals(asList("2", "5", "8", "B"), received.get(2));
	}

	@Test
	void testRowIterator() {
		Iterator<Collection<String>> iterator = table.rowIterator();
		List<Collection<String>> received = new ArrayList<>();
		while (iterator.hasNext()) {
			received.add(iterator.next());
		}
		assertEquals(asList("0", "1", "2"), received.get(0));
		assertEquals(asList("3", "4", "5"), received.get(1));
		assertEquals(asList("6", "7", "8"), received.get(2));
		assertEquals(asList("9", "A", "B"), received.get(3));
	}

	@Test
	void testStream() {
		List<String> list = table.stream().collect(Collectors.toList());
		assertEquals(asList(
				"0", "1", "2",
				"3", "4", "5",
				"6", "7", "8",
				"9", "A", "B"
		), list);
	}
}
