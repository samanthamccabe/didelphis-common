/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.common.structures.tables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/27/2015
 */
class DataTableTest {
	
	private static DataTable<String> table;

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
	void testGetRowAsMap() {
		Map<String, String> rowAsMap = table.getRowAsMap(0);
		
		assertEquals("1", rowAsMap.get("X"));
		assertEquals("a", rowAsMap.get("Y"));
		assertEquals("L", rowAsMap.get("Z"));
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
		assertEquals("0", table.get(1,1));
	}

	@Test
	void testGetNumberRows() {
		assertEquals(3, table.getRows());
	}

	@Test
	void testGetNumberColumns() {
		assertEquals(3, table.getColumns());
	}

	@BeforeEach
	void initTable() {
		table = createTable();
	}

	@Test
	void testConstructor() {
		Map<String, List<String>> map = new LinkedHashMap<>();

		List<String> list1 = new ArrayList<>();
		list1.add("a");
		list1.add("b");

		List<String> list2 = new ArrayList<>();
		list2.add("c");
		list2.add("d");

		map.put("X", list1);
		map.put("Y", list2);

		DataTable<String> dataTable = new DataTable<>(map);

		// Testing Keys
		List<String> receivedKeys = dataTable.getKeys();
		List<String> expectedKeys = new ArrayList<>();
		expectedKeys.add("X");
		expectedKeys.add("Y");

		assertEquals(expectedKeys, receivedKeys);

		// 
		List<String> columnX = dataTable.getColumn("X");
		List<String> columnY = dataTable.getColumn("Y");

		assertEquals(list1, columnX);
		assertEquals(list2, columnY);
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
		assertEquals("2", table.get(0, 1));
		assertEquals("3", table.get(0, 2));

		assertEquals("a", table.get(1, 0));
		assertEquals("b", table.get(1, 1));
		assertEquals("c", table.get(1, 2));
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
		table2.set(2,2,"XXX");

		assertEquals(table.hashCode(), table1.hashCode());
		assertNotEquals(table.hashCode(), table2.hashCode());
	}


	@Test
	void testToString() {
		DataTable<String> table1 = new DataTable<>(table);
		DataTable<String> table2 = new DataTable<>(table);
		table2.set(2,2,"XXX");

		assertEquals(table.toString(), table1.toString());
		assertNotEquals(table.toString(), table2.toString());
	}
	
	@Test
	void testIterator() {
		Set<List<String>> received = new HashSet<>();
		table.iterator().forEachRemaining(received::add);

		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		List<String> list3 = new ArrayList<>();

		Collections.addAll(list1, "1", "a", "L");
		Collections.addAll(list2, "2", "b", "M");
		Collections.addAll(list3, "3", "c", "N");
		
		Set<List<String>>  expected = new HashSet<>();
		Collections.addAll(expected, list1, list2, list3);
		assertEquals(expected, received);
	}
	
	private static DataTable<String> createTable() {
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		List<String> list3 = new ArrayList<>();

		Collections.addAll(list1, "1", "2", "3");
		Collections.addAll(list2, "a", "b", "c");
		Collections.addAll(list3, "L", "M", "N");

		// If this is NOT a LinkedHashMap the get(i,j) method will not work correctly
		Map<String, List<String>> map = new LinkedHashMap<>();

		map.put("X", list1);
		map.put("Y", list2);
		map.put("Z", list3);

		return new DataTable<>(map);
	}
}
