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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/1/17.
 */
class RectangularTableTest {

	private static RectangularTable<String> table;
	
	@BeforeEach
	void init() {
		List<List<String>> data = new ArrayList<>();
		data.add(Arrays.asList( "0", "1", "2"));
		data.add(Arrays.asList( "3", "4", "5"));
		data.add(Arrays.asList( "6", "7", "8"));
		data.add(Arrays.asList( "9", "A", "B"));

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
	void constructor1_IndexOutOfBounds() {
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
	void set_IndexOutOfBounds() {
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
		assertEquals(Arrays.asList("0", "1", "2"), table.getRow(0));
		assertEquals(Arrays.asList("3", "4", "5"), table.getRow(1));
		assertEquals(Arrays.asList("6", "7", "8"), table.getRow(2));
		assertEquals(Arrays.asList("9", "A", "B"), table.getRow(3));
	}

	@Test
	void getRow_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getRow(-1));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getRow(5));
	}

	@Test
	void getColumn() {
		assertEquals(Arrays.asList("0", "3", "6", "9"), table.getColumn(0));
		assertEquals(Arrays.asList("1", "4", "7", "A"), table.getColumn(1));
		assertEquals(Arrays.asList("2", "5", "8", "B"), table.getColumn(2));
	}

	@Test
	void getColumn_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getColumn(-1));
		assertThrows(IndexOutOfBoundsException.class,
		             () -> table.getColumn(5));
	}

	@Test
	void setRow() {
		assertEquals(Arrays.asList("0", "1", "2"), table.setRow(0, Arrays.asList("!", "%", "W")));
		assertEquals(Arrays.asList("3", "4", "5"), table.setRow(1, Arrays.asList("@", "^", "X")));
		assertEquals(Arrays.asList("6", "7", "8"), table.setRow(2, Arrays.asList("#", "&", "Y")));
		assertEquals(Arrays.asList("9", "A", "B"), table.setRow(3, Arrays.asList("$", "*", "Z")));

		assertEquals(Arrays.asList("!", "%", "W"), table.getRow(0)) ;
		assertEquals(Arrays.asList("@", "^", "X"), table.getRow(1)) ;
		assertEquals(Arrays.asList("#", "&", "Y"), table.getRow(2)) ;
		assertEquals(Arrays.asList("$", "*", "Z"), table.getRow(3)) ;
	}

	@Test
	void setRow_IllegalArgument() {
		assertThrows(IllegalArgumentException.class,
		             () -> table.setRow(0, new ArrayList<>()));
	}

	@Test
	void setColumn() {
		assertEquals(Arrays.asList("0", "3", "6", "9"), table.setColumn(0, Arrays.asList("X", "Y", "Z", "W")));
		assertEquals(Arrays.asList("1", "4", "7", "A"), table.setColumn(1, Arrays.asList("!", "@", "#", "$")));
		assertEquals(Arrays.asList("2", "5", "8", "B"), table.setColumn(2, Arrays.asList("%", "^", "&", "*")));

		assertEquals(Arrays.asList("X", "Y", "Z", "W"), table.getColumn(0));
		assertEquals(Arrays.asList("!", "@", "#", "$"), table.getColumn(1));
		assertEquals(Arrays.asList("%", "^", "&", "*"), table.getColumn(2));
	}

	@Test
	void setColumn_IllegalArgument() {
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
	void expand_2_1() {
		table.expand(2, 1);
		assertEquals(6, table.rows());
		assertEquals(4, table.columns());

		assertEquals(Arrays.asList("0", "1", "2", null), table.getRow(0));
		assertEquals(Arrays.asList("3", "4", "5", null), table.getRow(1));
		assertEquals(Arrays.asList(null, null, null, null), table.getRow(5));

		assertEquals(Arrays.asList("0", "3", "6", "9", null, null), table.getColumn(0));
	}

	@Test
	void expand_0_0() {
		table.expand(0, 0);
		assertEquals(4, table.rows());
		assertEquals(3, table.columns());
	}
	
	@Test
	void expand_IllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.expand(-1, 0));
		assertThrows(IllegalArgumentException.class,
				() -> table.expand(0, -1));
	}

	@Test
	void shrink_1_1() {
		table.shrink(1,1);
		assertEquals(3, table.rows());
		assertEquals(2, table.columns());

		assertEquals(Arrays.asList("0", "1"), table.getRow(0));
		assertEquals(Arrays.asList("3", "4"), table.getRow(1));
		assertEquals(Arrays.asList("6", "7"), table.getRow(2));

		assertEquals(Arrays.asList("0", "3", "6"), table.getColumn(0));
		assertEquals(Arrays.asList("1", "4", "7"), table.getColumn(1));
	}
	
	@Test
	void shrink_0_0() {
		table.shrink(0, 0);
		assertEquals(4, table.rows());
		assertEquals(3, table.columns());
	}
	
	@Test
	void shrink_IllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.shrink(-1, 0));
		assertThrows(IllegalArgumentException.class,
				() -> table.shrink(0, -1));
	}
	
	@Test
	void insertRowIndex0() {
		List<String> data = Arrays.asList("X", "Y", "Z");
		table.insertRow(0, data);

		assertEquals(data, table.getRow(0));
		assertEquals(Arrays.asList("0", "1", "2"), table.getRow(1));
		assertEquals(5, table.rows());
	}

	@Test
	void insertRowIndex1() {
		List<String> data = Arrays.asList("X", "Y", "Z");
		table.insertRow(1, data);

		assertEquals(Arrays.asList("0", "1", "2"), table.getRow(0));
		assertEquals(data, table.getRow(1));
		assertEquals(5, table.rows());
	}

	@Test
	void insertRow_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(-1, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(7, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertRow(5, new ArrayList<>()));
	}

	@Test
	void insertRow_IllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> table.insertRow(0, new ArrayList<>()));
	}
	
	@Test
	void insertColumnIndex0() {
		List<String> data = Arrays.asList("W", "X", "Y", "Z");
		table.insertColumn(0, data);

		assertEquals(4, table.columns());
		assertEquals(data, table.getColumn(0));
		assertEquals(Arrays.asList("0", "3", "6", "9"), table.getColumn(1));
	}

	@Test
	void insertColumnIndex1() {
		List<String> data = Arrays.asList("W", "X", "Y", "Z");
		table.insertColumn(1, data);
		assertEquals(4, table.columns());
		assertEquals(Arrays.asList("0", "3", "6", "9"), table.getColumn(0));
		assertEquals(data, table.getColumn(1));
		assertEquals(Arrays.asList("1", "4", "7", "A"), table.getColumn(2));
	}

	@Test
	void insertColumn_IndexOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(-1, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(7, new ArrayList<>()));
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.insertColumn(5, new ArrayList<>()));
	}

	@Test
	void removeRow() {
		assertEquals(Arrays.asList("0", "1", "2"), table.removeRow(0));
		assertEquals(3, table.rows());
		assertEquals(Arrays.asList("6", "7", "8"), table.removeRow(1));
		assertEquals(2, table.rows());

		assertEquals(Arrays.asList("3", "4", "5"), table.getRow(0));
	}

	@Test
	void removeColumnIndex0() {
		assertEquals(Arrays.asList("0", "3", "6", "9"), table.removeColumn(0));
		assertEquals(2, table.columns());
	}

	@Test
	void removeColumnIndex1() {
		assertEquals(Arrays.asList("1", "4", "7", "A"), table.removeColumn(1));
		assertEquals(2, table.columns());
	}

	@Test
	void removeColumnIndex2() {
		assertEquals(Arrays.asList("2", "5", "8", "B"), table.removeColumn(2));
		assertEquals(2, table.columns());
	}

	@Test
	void getDelegate() {
		assertEquals(
				Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"),
		        table.getDelegate()
		);
	}


}
