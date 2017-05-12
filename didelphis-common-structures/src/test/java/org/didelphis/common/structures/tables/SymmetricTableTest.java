package org.didelphis.common.structures.tables;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/14/17.
 */
class SymmetricTableTest {
	
	/* Sample Data Diagram *
	 *     0 1 2 3         *
	 *   ┌─────────┐       *
	 * 0 │ A       │       *
	 * 1 │ B C     │       *
	 * 2 │ D E F   │       *
	 * 3 │ G H J K │       *
	 *   └─────────┘       *
	 * * * * * * * * * * * */
	private SymmetricTable<String> table;
	
	@BeforeEach
	void init() {
		List<String> a = new ArrayList<>();
		Collections.addAll(a, "A", "B", "C", "D", "E", "F", "G", "H", "J", "K");
		table = new SymmetricTable<>(4, a);
	}
	
	@Test
	void constructorDefaultValue() {
		SymmetricTable<String>  table1 = new SymmetricTable<>("", 3);
		assertEquals("", table1.get(1, 1));
		assertEquals(3, table1.getColumns());
		assertEquals(3, table1.getRows());
	}
	
	@Test
	void constructorException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			List<String> a = new ArrayList<>();
			Collections.addAll(a, "A", "B");
			table = new SymmetricTable<>(3, a);
		});
	}
	
	@Test
	void get() {
		assertEquals("A", table.get(0, 0));
		assertEquals("B", table.get(1, 0));
		assertEquals("C", table.get(1, 1));
		assertEquals("D", table.get(2, 0));
		assertEquals("E", table.get(2, 1));
		assertEquals("F", table.get(2, 2));
		assertEquals("G", table.get(3, 0));
		assertEquals("H", table.get(3, 1));
		assertEquals("J", table.get(3, 2));
		assertEquals("K", table.get(3, 3));
		
		assertEquals("B", table.get(0, 1));
		assertEquals("C", table.get(1, 1));
		assertEquals("D", table.get(0, 2));
		assertEquals("E", table.get(1, 2));
		assertEquals("F", table.get(2, 2));
		assertEquals("G", table.get(0, 3));
		assertEquals("H", table.get(1, 3));
		assertEquals("J", table.get(2, 3));
		assertEquals("K", table.get(3, 3));
	}

	@Test
	void set() {
		table.set(0, 0, "W");
		table.set(1, 1, "X");
		table.set(2, 2, "Y");
		table.set(3, 3, "Z");
		
		assertEquals("W",table.get(0, 0));
		assertEquals("X",table.get(1, 1));
		assertEquals("Y",table.get(2, 2));
		assertEquals("Z",table.get(3, 3));
	}

	@Test
	void testHashCode() {
		SymmetricTable<String> table1 = new SymmetricTable<>(table);
		SymmetricTable<String> table2 = new SymmetricTable<>(table);

		table2.set(0, 0, "W");
		table2.set(0, 1, "X");
		table2.set(1, 2, "Y");
		table2.set(2, 3, "Z");
		
		assertEquals(table.hashCode(), table1.hashCode());
		assertNotEquals(table.hashCode(), table2.hashCode());
	}


	@Test
	void testToString() {
		SymmetricTable<String> table1 = new SymmetricTable<>(table);
		SymmetricTable<String> table2 = new SymmetricTable<>(table);

		table2.set(0, 0, "W");
		table2.set(0, 1, "X");
		table2.set(1, 2, "Y");
		table2.set(2, 3, "Z");

		assertEquals(table.toString(), table1.toString());
		assertNotEquals(table.toString(), table2.toString());
	}


	@Test
	void equals() {
		assertEquals(table, new SymmetricTable<>(table));
	}

	@Test
	void equalsIdentity() {
		assertEquals(table, table);
	}

	@Test
	void equalsNull() {
		assertNotEquals(null, table);
	}

	@Test
	void getNumberRows() {
		assertEquals(4, table.getRows());
	}

	@Test
	void getNumberColumns() {
		assertEquals(4, table.getColumns());
	}
}
