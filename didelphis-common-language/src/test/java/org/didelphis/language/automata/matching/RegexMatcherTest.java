package org.didelphis.language.automata.matching;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexMatcherTest {
	
	@Test
	void testMatches() {
		RegexMatcher matcher = new RegexMatcher();
		
		assertMatches(matcher, "abc", "a", 0);
		assertMatches(matcher, "abc", "b", 1);
		assertMatches(matcher, "abc", "c", 2);

		assertNotMatches(matcher, "abc", "a", 2);
		assertNotMatches(matcher, "abc", "b", 0);
		assertNotMatches(matcher, "abc", "c", 1);
	}

	private static void assertMatches(
			RegexMatcher matcher,
			String word1,
			@Language("RegExp") String word2,
			int index
	) {
		int matches = matcher.matches(word1, word2, index);
		assertTrue(word2.length() <= matches);
	}

	private static void assertNotMatches(
			RegexMatcher matcher,
			String word1,
			@Language("RegExp") String word2,
			int index
	) {
		int matches = matcher.matches(word1, word2, index);
		assertFalse(word2.length() <= matches);
	}

	@Test
	void testEquals() {

		RegexMatcher matcher1 = new RegexMatcher();
		RegexMatcher matcher2 = new RegexMatcher();
		
		assertEquals(matcher1, matcher2);
		assertEquals(matcher2, matcher1);
		assertNotEquals(matcher1, null);
		assertNotEquals(matcher2, null);
	}

	@Test
	void testHashCode() {
		RegexMatcher matcher1 = new RegexMatcher();
		RegexMatcher matcher2 = new RegexMatcher();

		assertEquals(matcher1.hashCode(), matcher2.hashCode());
		assertEquals(matcher2.hashCode(), matcher1.hashCode());
	}

	@Test
	void testToString() {

		RegexMatcher matcher1 = new RegexMatcher();
		RegexMatcher matcher2 = new RegexMatcher();

		assertEquals(matcher1.toString(), matcher2.toString());
		assertEquals(matcher2.toString(), matcher1.toString());
	}
}