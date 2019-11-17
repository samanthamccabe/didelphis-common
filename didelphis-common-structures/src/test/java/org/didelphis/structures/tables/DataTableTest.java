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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class DataTableTest {

	private static DataTable<String> table;

	@BeforeEach
	void init() {
		table = createTable();
	}

	@Test
	void testConstructor_keys() {
		List<String> keys = Arrays.asList("W", "X", "Y", "Z");
		Table<?> table = new DataTable<>(keys);
		assertEquals(0, table.rows());
		assertEquals(4, table.columns());
	}

	@Test
	void testHasKey() {
		assertTrue(table.hasKey("X"));
		assertTrue(table.hasKey("Y"));
		assertTrue(table.hasKey("Z"));
		assertFalse(table.hasKey("0"));
		assertFalse(table.hasKey("1"));
		assertFalse(table.hasKey("2"));
	}

	@Test
	void testGetKeys() {
		List<String> keys1 = table.getKeys();
		List<String> keys2 = new ArrayList<>();
		Collections.addAll(keys2, "X", "Y", "Z");
		assertEquals(keys2, keys1);
	}

	@Test
	void testGetRow() {
		List<String> row = table.getRow(0);
		List<String> keys2 = new ArrayList<>();
		Collections.addAll(keys2, "1", "a", "L");
		assertEquals(keys2, row);
	}

	@Test
	void testSet() {
		table.set(1, 1, "0");
		assertEquals("0", table.get(1, 1));
	}

	@Test
	void testGetNumberRows() {
		assertEquals(3, table.rows());
	}

	@Test
	void testGetNumberColumns() {
		assertEquals(3, table.columns());
	}

	@Test
	void testConstructor() {

		List<List<String>> rows = Arrays
				.asList(Arrays.asList("a", "c"), Arrays.asList("b", "d"));

		List<String> keys = Arrays.asList("X", "Y");

		DataTable<String> dataTable = new DataTable<>(keys, rows);

		// Testing Keys
		List<String> receivedKeys = dataTable.getKeys();

		assertEquals(keys, receivedKeys);

		//
		List<String> columnX = dataTable.getColumn("X");
		List<String> columnY = dataTable.getColumn("Y");

		assertEquals(Arrays.asList("a", "b"), columnX);
		assertEquals(Arrays.asList("c", "d"), columnY);
	}

	@Test
	void testEquals() {
		ColumnTable<String> table1 = createTable();
		ColumnTable<String> table2 = createTable();

		assertEquals(table1, table2);
	}

	@Test
	void testGet() {
		assertEquals("1", table.get(0, 0));
		assertEquals("2", table.get(1, 0));
		assertEquals("3", table.get(2, 0));

		assertEquals("a", table.get(0, 1));
		assertEquals("b", table.get(1, 1));
		assertEquals("c", table.get(2, 1));
	}

	@Test
	void testGetColumn() {
		Collection<String> list1 = new ArrayList<>();
		Collection<String> list2 = new ArrayList<>();
		Collection<String> list3 = new ArrayList<>();

		Collections.addAll(list1, "1", "2", "3");
		Collections.addAll(list2, "a", "b", "c");
		Collections.addAll(list3, "L", "M", "N");

		assertEquals(list1, table.getColumn("X"));
		assertEquals(list2, table.getColumn("Y"));
		assertEquals(list3, table.getColumn("Z"));
	}

	@Test
	void testGetBadColumn() {
		assertNull(table.getColumn("0"));
	}

	@Test
	void testGetRows() {
		Collection<String> row1 = new ArrayList<>();
		Collection<String> row2 = new ArrayList<>();
		Collection<String> row3 = new ArrayList<>();

		Collections.addAll(row1, "1", "a", "L");
		Collections.addAll(row2, "2", "b", "M");
		Collections.addAll(row3, "3", "c", "N");

		assertEquals(row1, table.getRow(0));
		assertEquals(row2, table.getRow(1));
		assertEquals(row3, table.getRow(2));
	}

	@Test
	void testIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, () -> table.getRow(3));
	}

	@Test
	void testHashCode() {
		DataTable<String> table1 = new DataTable<>(table);
		DataTable<String> table2 = new DataTable<>(table);
		table2.set(2, 2, "XXX");

		assertEquals(table.hashCode(), table1.hashCode());
		assertNotEquals(table.hashCode(), table2.hashCode());
	}

	@Test
	void testToString() {
		DataTable<String> table1 = new DataTable<>(table);
		DataTable<String> table2 = new DataTable<>(table);
		table2.set(2, 2, "XXX");

		assertEquals(table.toString(), table1.toString());
		assertNotEquals(table.toString(), table2.toString());
	}

	@Test
	void setColumnName() {
		assertEquals("X", table.setColumnKey(0, "P"));
		assertEquals("Y", table.setColumnKey(1, "Q"));
		assertEquals("Z", table.setColumnKey(2, "R"));

		assertEquals("P", table.getColumnKey(0));
		assertEquals("Q", table.getColumnKey(1));
		assertEquals("R", table.getColumnKey(2));
	}

	@Test
	void getColumnName() {
		assertEquals("X", table.getColumnKey(0));
		assertEquals("Y", table.getColumnKey(1));
		assertEquals("Z", table.getColumnKey(2));
	}

	@Test
	void setColumnName_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, ()-> table.setColumnKey(3, ""));
	}

	@Test
	void getColumnName_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, ()-> table.getColumnKey(3));
	}

	private static DataTable<String> createTable() {
		List<List<String>> list = Arrays.asList(
				Arrays.asList("1", "a", "L"),
				Arrays.asList("2", "b", "M"),
				Arrays.asList("3", "c", "N"));
		List<String> keys = Arrays.asList("X", "Y", "Z");
		return new DataTable<>(keys, list);
	}
}
