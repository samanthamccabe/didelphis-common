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

package org.didelphis.language.phonetic;

import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SequenceFactoryTest extends PhoneticTestBase {

	private SequenceFactory<Integer> testFactory;

	@BeforeEach
	void init() {
		testFactory = new SequenceFactory<>(
				loader.getFeatureMapping(),
				FormatterMode.INTELLIGENT
		);
	}

	@Test
	void apply() {
		assertEquals(factory.toSequence("a"), factory.apply("a"));
	}

	@Test
	void toSegment() {
		assertEquals(loader.getFeatureMapping().parseSegment("a"),
				factory.toSegment("a")
		);
	}

	@Test
	void toSegmentFeatures() {
		String string = "[+con, -son, -voice]";
		assertEquals(
				loader.getFeatureMapping().parseSegment(string),
				factory.toSegment(string)
		);
	}

	@Test
	void toSequenceFeatures() {
		String string = "$[+hgh]1k";
		assertEquals(
				loader.getFeatureMapping().parseSegment(string),
				factory.toSegment(string)
		);
	}

	@Test
	void toSequence() {
		Sequence<Integer> sequence = factory.toSequence("");
		sequence.add(factory.toSegment("a"));
		assertEquals(factory.toSequence("a"), sequence);
	}

	@Test
	void getSpecialStrings() {
		assertEquals(new HashSet<>(loader.getFeatureMapping().getSymbols()),
				new HashSet<>(factory.getSpecialStrings())
		);
	}

	@Test
	void getFeatureMapping() {
		assertEquals(loader.getFeatureMapping(), factory.getFeatureMapping());
	}

	@Test
	void getFormatterMode() {
		assertEquals(FormatterMode.INTELLIGENT, factory.getFormatterMode());
	}

	@Test
	void getReservedStrings() {
		assertTrue(factory.getReservedStrings().isEmpty());
	}

	@Test
	void testToString() {
		assertEquals(factory.toString(), testFactory.toString());
	}

	@Test
	void equals() {
		assertEquals(factory, testFactory);
	}

	@Test
	void testHashCode() {
		assertEquals(factory.hashCode(), testFactory.hashCode());
	}

	@Test
	void testGetSequence01() {
		String word = "avaːm";
		Sequence<Integer> sequence = factory.toSequence(word);
		assertTrue(!sequence.isEmpty());
	}

	@Test
	void testReservedConstructor() {
		Set<String> reserved = new HashSet<>();
		reserved.add("ph");
		reserved.add("th");
		reserved.add("kh");

		SequenceFactory<Integer> factory = new SequenceFactory<>(
				loader.getFeatureMapping(), 
				reserved,
				FormatterMode.NONE
		);

		List<String> strings = asList("a", "ph", "a", "th", "a", "kh", "a");
		Sequence<Integer> expected = factory.toSequence("");
		for (String string : strings) {
			expected.add(factory.toSequence(string));
		}

		Sequence<Integer> received = factory.toSequence("aphathakha");

		for (int i = 0; i < expected.size(); i++) {
			Segment<Integer> ex = expected.get(i);
			Segment<Integer> re = received.get(i);
			assertEquals(ex, re, "index: " + i);
		}

		assertEquals(expected, received);
	}

	@Test
	void testReservedMethod() {

		SequenceFactory<Integer> factory = new SequenceFactory<>(
				loader.getFeatureMapping(),
				FormatterMode.NONE
		);

		factory.reserve("ph");
		factory.reserve("th");
		factory.reserve("kh");

		List<String> strings = asList("a", "ph", "a", "th", "a", "kh", "a");
		Sequence<Integer> expected = factory.toSequence("");
		for (String string : strings) {
			expected.add(factory.toSequence(string));
		}

		Sequence<Integer> received = factory.toSequence("aphathakha");

		for (int i = 0; i < expected.size(); i++) {
			Segment<Integer> ex = expected.get(i);
			Segment<Integer> re = received.get(i);
			assertEquals(ex, re, "index: " + i);
		}

		assertEquals(expected, received);
	}
}
