package org.didelphis.language.automata.matching;

import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequenceMatcherTest extends PhoneticTestBase {

	private SequenceParser<Integer> parser = new SequenceParser<>(factory);
	
	@Test
	void testMatches() {
		SequenceMatcher<Integer> matcher1 = new SequenceMatcher<>(parser);
		MultiMap<String, Sequence<Integer>> map = new GeneralMultiMap<>();
		map.add("X", factory.toSequence("x"));
		map.add("X", factory.toSequence("y"));
		map.add("X", factory.toSequence("z"));
		SequenceParser<Integer> parser1 = new SequenceParser<>(factory, map);
		SequenceMatcher<Integer> matcher2 = new SequenceMatcher<>(parser1);
		
		assertMatches(matcher1, "abc", "a", 0);
		assertMatches(matcher1, "abc", "b", 1);
		assertMatches(matcher1, "abc", "c", 2);

		assertMatches(matcher2, "abc", "a", 0);
		assertMatches(matcher2, "abc", "b", 1);
		assertMatches(matcher2, "abc", "c", 2);

		assertMatches(matcher2, "x__", "X", 0);
		assertMatches(matcher2, "_y_", "X", 1);
		assertMatches(matcher2, "__z", "X", 2);

		assertNotMatches(matcher1, "x__", "X", 0);
		assertNotMatches(matcher1, "_y_", "X", 1);
		assertNotMatches(matcher1, "__z", "X", 2);

		assertNotMatches(matcher1, "abc", "a", 2);
		assertNotMatches(matcher1, "abc", "b", 0);
		assertNotMatches(matcher1, "abc", "c", 1);
	}

	private static void assertMatches(
			SequenceMatcher<Integer> matcher1,
			String word1,
			String word2,
			int index
	) {
		Sequence<Integer> sequence1 = factory.toSequence(word1);
		Sequence<Integer> sequence2 = factory.toSequence(word2);
		int matches = matcher1.matches(sequence1, sequence2, index);
		assertTrue(sequence2.size() <= matches);
	}

	private static void assertNotMatches(
			SequenceMatcher<Integer> matcher1,
			String word1,
			String word2,
			int index
	) {
		Sequence<Integer> sequence1 = factory.toSequence(word1);
		Sequence<Integer> sequence2 = factory.toSequence(word2);
		int matches = matcher1.matches(sequence1, sequence2, index);
		assertFalse(sequence2.size() <= matches);
	}

	@Test
	void testEquals() {

		SequenceMatcher<?> matcher1 = new SequenceMatcher<>(parser);
		SequenceMatcher<?> matcher2 = new SequenceMatcher<>(parser);
		MultiMap<String, Sequence<Integer>> map = new GeneralMultiMap<>();
		map.add("X", factory.toSequence("x"));
		map.add("X", factory.toSequence("y"));
		SequenceParser<Integer> parser1 = new SequenceParser<>(factory, map);
		SequenceMatcher<?> matcher3 = new SequenceMatcher<>(parser1);
		
		assertEquals(matcher1, matcher2);
		assertEquals(matcher2, matcher1);
		assertNotEquals(matcher1, matcher3);
		assertNotEquals(matcher2, matcher3);
		assertNotEquals(matcher3, matcher1);
		assertNotEquals(matcher3, matcher2);

		assertNotEquals(matcher1, null);
		assertNotEquals(matcher2, null);
		assertNotEquals(matcher3, null);
	}

	@Test
	void testHashCode() {
		SequenceMatcher<?> matcher1 = new SequenceMatcher<>(parser);
		SequenceMatcher<?> matcher2 = new SequenceMatcher<>(parser);
		MultiMap<String, Sequence<Integer>> map = new GeneralMultiMap<>();
		map.add("X", factory.toSequence("x"));
		map.add("X", factory.toSequence("y"));
		SequenceParser<Integer> parser1 = new SequenceParser<>(factory, map);
		SequenceMatcher<?> matcher3 = new SequenceMatcher<>(parser1);

		assertEquals(matcher1.hashCode(), matcher2.hashCode());
		assertEquals(matcher2.hashCode(), matcher1.hashCode());
		assertNotEquals(matcher1.hashCode(), matcher3.hashCode());
		assertNotEquals(matcher2.hashCode(), matcher3.hashCode());
		assertNotEquals(matcher3.hashCode(), matcher1.hashCode());
		assertNotEquals(matcher3.hashCode(), matcher2.hashCode());
	}

	@Test
	void testToString() {

		SequenceMatcher<?> matcher1 = new SequenceMatcher<>(parser);
		SequenceMatcher<?> matcher2 = new SequenceMatcher<>(parser);
		MultiMap<String, Sequence<Integer>> map = new GeneralMultiMap<>();
		map.add("X", factory.toSequence("x"));
		map.add("X", factory.toSequence("y"));
		SequenceParser<Integer> parser1 = new SequenceParser<>(factory, map);
		SequenceMatcher<?> matcher3 = new SequenceMatcher<>(parser1);

		assertEquals(matcher1.toString(), matcher2.toString());
		assertEquals(matcher2.toString(), matcher1.toString());
		assertNotEquals(matcher1.toString(), matcher3.toString());
		assertNotEquals(matcher2.toString(), matcher3.toString());
		assertNotEquals(matcher3.toString(), matcher1.toString());
		assertNotEquals(matcher3.toString(), matcher2.toString());
	}
}