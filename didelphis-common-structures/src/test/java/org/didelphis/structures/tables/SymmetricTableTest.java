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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class SymmetricTableTest {

	/* Sample Data Diagram *
	 *     0 1 2 3         *
	 *   ┌─────────┐       *
	 * 0 │ A B D G │       *
	 * 1 │ B C E H │       *
	 * 2 │ D E F J │       *
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
		assertEquals(3, table1.columns());
		assertEquals(3, table1.rows());
	}

	@Test
	void constructorException() {
		assertThrows(IllegalArgumentException.class, () -> {
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
		assertEquals(4, table.rows());
	}

	@Test
	void getNumberColumns() {
		assertEquals(4, table.columns());
	}

	@Test
	void getRow() {
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.getRow(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), table.getRow(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.getRow(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), table.getRow(3));
	}

	@Test
	void getColumn() {
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.getColumn(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), table.getColumn(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.getColumn(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), table.getColumn(3));
	}

	@Test
	void setRow() {
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.setRow(0, Arrays.asList("!", "@", "#", "$")));
		assertEquals(Arrays.asList("@", "C", "E", "H"), table.setRow(1, Arrays.asList("@", "%", "^", "&")));
		assertEquals(Arrays.asList("#", "^", "F", "J"), table.setRow(2, Arrays.asList("#", "^", "*", "+")));
		assertEquals(Arrays.asList("$", "&", "+", "K"), table.setRow(3, Arrays.asList("$", "&", "+", "=")));

		assertEquals(Arrays.asList("!", "@", "#", "$"), table.getRow(0));
		assertEquals(Arrays.asList("@", "%", "^", "&"), table.getRow(1));
		assertEquals(Arrays.asList("#", "^", "*", "+"), table.getRow(2));
		assertEquals(Arrays.asList("$", "&", "+", "="), table.getRow(3));
	}

	@Test
	void setColumn() {
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.setColumn(0, Arrays.asList("!", "@", "#", "$")));
		assertEquals(Arrays.asList("@", "C", "E", "H"), table.setColumn(1, Arrays.asList("@", "%", "^", "&")));
		assertEquals(Arrays.asList("#", "^", "F", "J"), table.setColumn(2, Arrays.asList("#", "^", "*", "+")));
		assertEquals(Arrays.asList("$", "&", "+", "K"), table.setColumn(3, Arrays.asList("$", "&", "+", "=")));

		assertEquals(Arrays.asList("!", "@", "#", "$"), table.getColumn(0));
		assertEquals(Arrays.asList("@", "%", "^", "&"), table.getColumn(1));
		assertEquals(Arrays.asList("#", "^", "*", "+"), table.getColumn(2));
		assertEquals(Arrays.asList("$", "&", "+", "="), table.getColumn(3));
	}

	@Test
	void size() {
		assertEquals(16, table.size());
	}

	@Test
	void isEmpty() {
		assertFalse(table.isEmpty());
	}

	@Test
	void clear() {
		table.clear();
		assertTrue(table.isEmpty());
	}

	@Test
	void expand_1_0() {
		table.expand(1, 0, "");
		assertEquals(Arrays.asList("A", "B", "D", "G", ""), table.getRow(0));
		assertEquals(Arrays.asList("B", "C", "E", "H", ""), table.getRow(1));
		assertEquals(Arrays.asList("D", "E", "F", "J", ""), table.getRow(2));
		assertEquals(Arrays.asList("G", "H", "J", "K", ""), table.getRow(3));
		assertEquals(Collections.nCopies(5, ""), table.getRow(4));
	}

	@Test
	void expand_1_1() {
		table.expand(1, 1, "");
		assertEquals(Arrays.asList("A", "B", "D", "G", ""), table.getRow(0));
	}

	@Test
	void expand_0_0() {
		table.expand(0, 0,"" ); // NO OP
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.getRow(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), table.getRow(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.getRow(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), table.getRow(3));
	}

	@Test
	void expand_2_0() {
		table.expand(2, 0, "");
		assertEquals(Arrays.asList("A", "B", "D", "G", "", ""), table.getRow(0));
	}

	@Test
	void shrink_1_1() {
		table.shrink(1, 1);
		assertEquals(Arrays.asList("A", "B", "D"), table.getRow(0));
		assertEquals(Arrays.asList("B", "C", "E"), table.getRow(1));
		assertEquals(Arrays.asList("D", "E", "F"), table.getRow(2));
	}

	@Test
	void shrink_0_0() {
		table.shrink(0, 0); // NO OP
		assertEquals(Arrays.asList("A", "B", "D", "G"), table.getRow(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), table.getRow(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.getRow(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), table.getRow(3));

	}

	@Test
	void shrink_2_2() {
		table.shrink(2, 2);
		assertEquals(Arrays.asList("A", "B"), table.getRow(0));
		assertEquals(Arrays.asList("B", "C"), table.getRow(1));
	}

	@Test
	void shrink_4_4() {
		table.shrink(4, 4);
		assertTrue(table.isEmpty());
	}

	@Test
	void shrink_5_5() {
		assertThrows(IndexOutOfBoundsException.class,
				() -> table.shrink(5, 5) );
	}

	/* Sample Data Diagram *
	 *     0 1 2 3 4       *
	 *   ┌───────────┐     *
	 * 0 │ A         │     *
	 * 1 │ B C       │     *
	 * 2 │ # # #     │     *
	 * 3 │ D E # F   │     *
	 * 4 │ G H # J K │     *
	 *   └───────────┘     *
	 * * * * * * * * * * * */
	@Test
	void insertRow_1() {
		List<String> list = Arrays.asList("V", "W", "X", "Y", "Z");
		table.insertRow(1, list);
		assertEquals(5, table.rows());
		assertEquals(list, table.getRow(1));
		assertEquals(Arrays.asList("B", "X", "C", "E", "H"), table.getRow(2));
	}

	@Test
	void insertRow_2() {
		List<String> list = Arrays.asList("V", "W", "X", "Y", "Z");
		table.insertRow(2, list);
		assertEquals(5, table.rows());
		assertEquals(list, table.getRow(2));
		assertEquals(Arrays.asList("B", "C", "W", "E", "H"), table.getRow(1));
		assertEquals(Arrays.asList("G", "H", "Z", "J", "K"), table.getRow(4));
	}

	@Test
	void insertColumn_2() {
		List<String> list = Arrays.asList("V", "W", "X", "Y", "Z");
		table.insertColumn(2, list);
		assertEquals(5, table.columns());
		assertEquals(list, table.getColumn(2));
		assertEquals(Arrays.asList("B", "C", "W", "E", "H"), table.getColumn(1));
		assertEquals(Arrays.asList("G", "H", "Z", "J", "K"), table.getColumn(4));
	}

	@Test
	void removeRow_2() {
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.removeRow(2));
		assertEquals(Arrays.asList("G", "H", "K"), table.getRow(2));
	}

	@Test
	void removeColumn() {
		assertEquals(Arrays.asList("D", "E", "F", "J"), table.removeColumn(2));
		assertEquals(Arrays.asList("G", "H", "K"), table.getColumn(2));
	}

	@Test
	void testColumnIterator() {
		Iterator<Collection<String>> iterator = table.columnIterator();
		List<Collection<String>> received = new ArrayList<>();
		while (iterator.hasNext()) {
			received.add(iterator.next());
		}
		assertEquals(Arrays.asList("A", "B", "D", "G"), received.get(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), received.get(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), received.get(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), received.get(3));
	}

	@Test
	void testRowIterator() {
		Iterator<Collection<String>> iterator = table.rowIterator();
		List<Collection<String>> received = new ArrayList<>();
		while (iterator.hasNext()) {
			received.add(iterator.next());
		}
		assertEquals(Arrays.asList("A", "B", "D", "G"), received.get(0));
		assertEquals(Arrays.asList("B", "C", "E", "H"), received.get(1));
		assertEquals(Arrays.asList("D", "E", "F", "J"), received.get(2));
		assertEquals(Arrays.asList("G", "H", "J", "K"), received.get(3));
	}

	@Test
	void testStream() {
		List<String> list = table.stream().collect(Collectors.toList());
		assertEquals(Arrays.asList(
				"A", "B", "C", "D", "E", "F", "G", "H", "J", "K"
		), list);
	}
}
