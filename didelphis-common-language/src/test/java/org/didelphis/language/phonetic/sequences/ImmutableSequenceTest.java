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
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 * Date: 2017-06-23
 */
class ImmutableSequenceTest {

	private static Sequence<Integer> sequence;
	private static SequenceFactory<Integer> factory;

	@BeforeAll
	static void init() {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE);
		factory = new SequenceFactory<>(loader.getFeatureMapping(), FormatterMode.NONE);
		sequence = new ImmutableSequence<>(factory.getSequence("foob"));
	}

	@Test
	void addAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.addAll(Arrays.asList(
						factory.getSegment("f"),
						factory.getSegment("x"))));
	}

	@Test
	void replaceAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.replaceAll(
						factory.getSequence("oo"),
						factory.getSequence("y")));
	}

	@Test
	void sort() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.sort(Comparator.naturalOrder()));
	}

	@Test
	void set() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.set(1, factory.getSegment("y")));
	}

	@Test
	void add() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.add(factory.getSegment("y")));
	}

	@Test
	void remove_index() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.remove(1));
	}

	@Test
	void remove_object() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.remove(factory.getSegment("b")));
	}

	@Test
	void add_index() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.add(1, factory.getSegment("y")));
	}

	@Test
	void insert() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.insert(factory.getSequence("y"), 1));
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
		Sequence<Integer> foob = new ImmutableSequence<>(factory.getSequence("foob"));
		assertEquals(sequence, foob);
		assertNotEquals(sequence, factory.getSequence("foob"));
	}

	@Test
	void addAll1() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.addAll(2,Arrays.asList(
						factory.getSegment("f"),
						factory.getSegment("x"))));
	}

	@Test
	void removeAll() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.removeAll(Arrays.asList(
						factory.getSegment("f"),
						factory.getSegment("b"))));
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
						factory.getSegment("f"),
						factory.getSegment("b"))));
	}

	@Test
	void clear() {
		assertThrows(UnsupportedOperationException.class,
				()->sequence.clear());
	}

	@Test
	void testHashCode() {
		Sequence<Integer> foob = new ImmutableSequence<>(factory.getSequence("foob"));
		assertEquals(sequence.hashCode(), foob.hashCode());
		assertNotEquals(sequence.hashCode(), factory.getSequence("foob").hashCode());
	}

	@Test
	void testToString() {
		Sequence<Integer> foob = new ImmutableSequence<>(factory.getSequence("foob"));
		assertEquals(sequence.toString(), foob.toString());
		assertNotEquals(sequence.toString(), factory.getSequence("foob").toString());
	}

	@Test
	void add2() {
		assertThrows(UnsupportedOperationException.class,
				()-> sequence.add(factory.getSequence("x")));
	}

}