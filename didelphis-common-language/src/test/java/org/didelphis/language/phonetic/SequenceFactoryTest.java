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

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.sequences.Sequence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.*;
import static org.didelphis.language.parsing.FormatterMode.*;
import static org.junit.jupiter.api.Assertions.*;

class SequenceFactoryTest {

	private final FeatureModelLoader loader;
	private final SequenceFactory factory;
	private final FeatureMapping featureMapping;

	SequenceFactoryTest() {
		loader = new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				"AT_hybrid.model"
		);
		factory = new SequenceFactory(
				loader.getFeatureMapping(),
				INTELLIGENT
		);
		featureMapping = loader.getFeatureMapping();
	}

	@Test
	void testToSegment() {
		assertEquals(
				featureMapping.parseSegment("a"),
				factory.toSegment("a")
		);
	}

	@Test
	@DisplayName("Test parsing a segment from a feature array")
	void testToSegmentFeatures() {
		String string = "[+con, -son, -voice]";
		assertEquals(
				featureMapping.parseSegment(string),
				factory.toSegment(string)
		);
	}

	@Test
	@DisplayName ("Test that null parameters are not allowed")
	@SuppressWarnings ("ConstantConditions")
	void testParseNull() {
		assertThrows(
				NullPointerException.class,
				() -> factory.toSegment(null)
		);
		assertThrows(
				NullPointerException.class,
				() -> factory.toSequence(null)
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
		Sequence sequence = factory.toSequence("");
		sequence.add(factory.toSegment("a"));
		assertEquals(factory.toSequence("a"), sequence);
	}

	@Test
	void getSpecialStrings() {
		assertEquals(
				new HashSet<>(loader.getFeatureMapping().getSymbols()),
				new HashSet<>(factory.getSpecialStrings())
		);
	}

	@Test
	void getFeatureMapping() {
		assertEquals(loader.getFeatureMapping(), factory.getFeatureMapping());
	}

	@Test
	void getFormatterMode() {
		assertEquals(INTELLIGENT, factory.getFormatterMode());
	}

	@Test
	void getReservedStrings() {
		assertTrue(factory.getReservedStrings().isEmpty());
	}

	@Test
	void testToString() {
		assertEquals(factory.toString(), factory.toString());
	}

	@Test
	void testEquals() {
		FeatureMapping mapping = loader.getFeatureMapping();
		assertEquals(factory, new SequenceFactory(mapping, INTELLIGENT));
		assertNotEquals(factory, new SequenceFactory(mapping, COMPOSITION));
	}

	@Test
	void testHashCode() {
		assertEquals(factory.hashCode(), factory.hashCode());
	}

	@Test
	void testGetSequence01() {
		String word = "avaːm";
		Sequence sequence = factory.toSequence(word);
		assertFalse(sequence.isEmpty());
	}

	@Test
	void testReservedConstructor() {
		Set<String> reserved = new HashSet<>();
		reserved.add("ph");
		reserved.add("th");
		reserved.add("kh");

		SequenceFactory aFactory = new SequenceFactory(
				loader.getFeatureMapping(),
				reserved,
				NONE
		);

		List<String> strings = asList("a", "ph", "a", "th", "a", "kh", "a");
		Sequence expected = aFactory.toSequence("");
		for (String string : strings) {
			expected.add(aFactory.toSequence(string));
		}

		Sequence received = aFactory.toSequence("aphathakha");

		for (int i = 0; i < expected.size(); i++) {
			Segment ex = expected.get(i);
			Segment re = received.get(i);
			assertEquals(ex, re, "index: " + i);
		}

		assertEquals(expected, received);
	}

	@Test
	void testReservedMethod() {

		SequenceFactory aFactory = new SequenceFactory(
				loader.getFeatureMapping(),
				NONE
		);

		aFactory.reserve("ph");
		aFactory.reserve("th");
		aFactory.reserve("kh");

		List<String> strings = asList("a", "ph", "a", "th", "a", "kh", "a");
		Sequence expected = aFactory.toSequence("");
		for (String string : strings) {
			expected.add(aFactory.toSequence(string));
		}

		Sequence received = aFactory.toSequence("aphathakha");

		for (int i = 0; i < expected.size(); i++) {
			Segment ex = expected.get(i);
			Segment re = received.get(i);
			assertEquals(ex, re, "index: " + i);
		}

		assertEquals(expected, received);
	}
}
