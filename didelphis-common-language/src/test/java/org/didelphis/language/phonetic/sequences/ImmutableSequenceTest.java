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

package org.didelphis.language.phonetic.sequences;

import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code ImmutableSequenceTest}
 *
 * @since 0.1.0
 */
class ImmutableSequenceTest {

	private static Sequence<Integer> sequence;
	private static SequenceFactory<Integer> factory;

	@BeforeAll
	static void init() {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE);
		factory = new SequenceFactory<>(loader.getFeatureMapping(), FormatterMode.NONE);
		sequence = new ImmutableSequence<>(factory.toSequence("foob"));
	}

	@Test
	void addAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.addAll(Arrays.asList(
						factory.toSegment("f"),
						factory.toSegment("x"))));
	}

	@Test
	void replaceAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.replaceAll(
						factory.toSequence("oo"),
						factory.toSequence("y")));
	}

	@Test
	void sort() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.sort(Comparator.naturalOrder()));
	}

	@Test
	void set() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.set(1, factory.toSegment("y")));
	}

	@Test
	void add() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.add(factory.toSegment("y")));
	}

	@Test
	void remove_index() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.remove(1));
	}

	@Test
	void remove_object() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.remove(factory.toSegment("b")));
	}

	@Test
	void add_index() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.add(1, factory.toSegment("y")));
	}

	@Test
	void insert() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.insert(factory.toSequence("y"), 1));
	}

	@Test
	void replaceAll_function() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.replaceAll((x)->x));
	}

	@Test
	void remove2() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.remove(0,1));
	}

	@Test
	void equals() {
		Sequence<Integer> foob = new ImmutableSequence<>(factory.toSequence("foob"));
		assertEquals(sequence, foob);
		assertNotEquals(sequence, factory.toSequence("foob"));
	}

	@Test
	void addAll1() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.addAll(2,Arrays.asList(
						factory.toSegment("f"),
						factory.toSegment("x"))));
	}

	@Test
	void removeAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.removeAll(Arrays.asList(
						factory.toSegment("f"),
						factory.toSegment("b"))));
	}

	@Test
	void removeIf() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.removeIf((x)->true));
	}

	@Test
	void retainAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.retainAll(Arrays.asList(
						factory.toSegment("f"),
						factory.toSegment("b"))));
	}

	@Test
	void clear() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.clear());
	}

	@Test
	void testHashCode() {
		Sequence<Integer> foob = new ImmutableSequence<>(factory.toSequence("foob"));
		assertEquals(sequence.hashCode(), foob.hashCode());
		assertNotEquals(sequence.hashCode(), factory.toSequence("foob").hashCode());
	}

	@Test
	void testToString() {
		Sequence<Integer> foob = new ImmutableSequence<>(factory.toSequence("foob"));
		assertEquals(sequence.toString(), foob.toString());
		assertNotEquals(sequence.toString(), factory.toSequence("foob").toString());
	}

	@Test
	void add2() {
		assertThrows(UnsupportedOperationException.class,
				()-> sequence.add(factory.toSequence("x")));
	}

}
