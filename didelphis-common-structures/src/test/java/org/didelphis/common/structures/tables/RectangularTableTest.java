package org.didelphis.common.structures.tables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/1/17.
 */
class RectangularTableTest {

	private static RectangularTable<String> table;
	
	@BeforeEach
	void init() {
		List<List<String>> data = new ArrayList<>();
		List<String> row1 = new ArrayList<>();
		List<String> row2 = new ArrayList<>();
		List<String> row3 = new ArrayList<>();
		List<String> row4 = new ArrayList<>();

		Collections.addAll(row1, "0", "1", "2");
		Collections.addAll(row2, "3", "4", "5");
		Collections.addAll(row3, "6", "7", "8");
		Collections.addAll(row4, "9", "A", "B");
		Collections.addAll(data, row1, row2, row3, row4);

		table = new RectangularTable<>(data, 4, 3);
	}
	
	@Test
	void constructor1() {
		RectangularTable<String> table1 = new RectangularTable<>("X", 2, 2);
		assertEquals("X", table1.get(0, 0));
		assertEquals("X", table1.get(0, 1));
		assertEquals("X", table1.get(1, 0));
		assertEquals("X", table1.get(1, 1));
	}

	@Test
	void constructor2() {
		List<List<String>> data = new ArrayList<>();
		List<String> row1 = new ArrayList<>();
		Collections.addAll(row1, "0", "1", "2");
		List<String> row2 = new ArrayList<>();
		Collections.addAll(row2, "3", "4", "5");
		Collections.addAll(data, row1, row2);

		RectangularTable<String> table1 = new RectangularTable<>(data, 2, 3);
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
		assertEquals("9", table.get(3, 0));
		assertEquals("A", table.get(3, 1));
		assertEquals("B", table.get(3, 2));
	}

	@Test
	void set() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		table1.set(3, 0, "X");
		table1.set(3, 1, "Y");
		table1.set(3, 2, "Z");
		
		assertEquals("X", table1.get(3, 0));
		assertEquals("Y", table1.get(3, 1));
		assertEquals("Z", table1.get(3, 2));
	}

	@Test
	void equals() {
		RectangularTable<String> table1 = new RectangularTable<>(table);
		assertEquals(table1, table);
	}

	@Test
	void getNumberRows() {
		assertEquals(4, table.getRows());
	}

	@Test
	void getNumberColumns() {
		assertEquals(3, table.getColumns());
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
}
