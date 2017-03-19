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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/27/2015
 */
class DataTableTest {

	private static final ColumnTable<String> TABLE = createTable();

	@Test
	void testGet() {
		assertEquals("1", TABLE.get(0, 0));
		assertEquals("2", TABLE.get(0, 1));
		assertEquals("3", TABLE.get(0, 2));
		
		assertEquals("a", TABLE.get(1, 0));
		assertEquals("b", TABLE.get(1, 1));
		assertEquals("c", TABLE.get(1, 2));
	}
	
	@Test
	void testGetColumn() {
		Collection<String> list1 = new ArrayList<>();
		Collection<String> list2 = new ArrayList<>();
		Collection<String> list3 = new ArrayList<>();

		Collections.addAll(list1, "1", "2", "3");
		Collections.addAll(list2, "a", "b", "c");
		Collections.addAll(list3, "L", "M", "N");
		
		assertEquals(list1, TABLE.getColumn("X"));
		assertEquals(list2, TABLE.getColumn("Y"));
		assertEquals(list3, TABLE.getColumn("Z"));
	}
	
	@Test
	void testGetBadColumn() {
		assertNull(TABLE.getColumn("0"));
	}
	
	@Test
	void testGetRows() {
		Collection<String> row1 = new ArrayList<>();
		Collection<String> row2 = new ArrayList<>();
		Collection<String> row3 = new ArrayList<>();

		Collections.addAll(row1, "1", "a", "L");
		Collections.addAll(row2, "2", "b", "M");
		Collections.addAll(row3, "3", "c", "N");
		
		assertEquals(row1, TABLE.getRow(0));
		assertEquals(row2, TABLE.getRow(1));
		assertEquals(row3, TABLE.getRow(2));
	}
	
	@Test
	void testIndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, () -> TABLE.getRow(3));

	}

	private static ColumnTable<String> createTable() {
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
