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
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.segments.Segment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhoneticSequenceTest extends PhoneticTestBase {

	@Test
	void testEquals() {
		Sequence<Integer> sequence1 = factory.toSequence("z");
		Sequence<Integer> sequence2 = factory.toSequence("y");
		Sequence<Integer> sequence3 = factory.toSequence("y");

		assertEquals(sequence1, sequence1);
		assertEquals(sequence3, sequence2);
		assertNotEquals(sequence2, sequence1);
		assertNotEquals(sequence3, sequence1);
		assertNotEquals(null, sequence1);
	}

	@Test
	void testHashcode() {
		Sequence<Integer> sequence1 = factory.toSequence("z");
		Sequence<Integer> sequence2 = factory.toSequence("y");
		Sequence<Integer> sequence3 = factory.toSequence("y");

		assertEquals(sequence1.hashCode(), sequence1.hashCode());
		assertEquals(sequence3.hashCode(), sequence2.hashCode());
		assertNotEquals(sequence2.hashCode(), sequence1.hashCode());
		assertNotEquals(sequence3.hashCode(), sequence1.hashCode());
	}

	@Test
	void testCompareTo() {
		Sequence<Integer> sequence1 = factory.toSequence("k");
		Sequence<Integer> sequence2 = factory.toSequence("g");
		Sequence<Integer> sequence3 = factory.toSequence("x");
		Sequence<Integer> sequence4 = factory.toSequence("kx");

		assertEquals(-1, sequence1.compareTo(sequence2));
		assertEquals(-1, sequence1.compareTo(sequence3));
		assertEquals(-1, sequence2.compareTo(sequence3));

		assertEquals(-1, sequence1.compareTo(sequence4));

		assertEquals(-1, sequence4.compareTo(sequence2));
		assertEquals(-1, sequence4.compareTo(sequence3));
	}

	@Test
	void testMatches01() {
		Sequence<Integer> sequence = factory.toSequence("an");
		assertMatches(sequence, factory.toSequence("an"));
		assertNotMatches(sequence, factory.toSequence("aa"));
	}

	@Test
	void testMatches02() {
		Sequence<Integer> sequence = factory.toSequence("a");

		assertMatches(sequence, factory.toSequence("a"));
		assertNotMatches(sequence, factory.toSequence("an"));
		assertNotMatches(sequence, factory.toSequence("n"));
		assertNotMatches(sequence, factory.toSequence("b"));
		assertNotMatches(sequence, factory.toSequence("e"));
		assertNotMatches(sequence, factory.toSequence("c"));
	}

	@Test
	void testMatches03() {
		Sequence<Integer> sequence = factory.toSequence("a[-continuant, -son]");

		assertMatches(sequence, factory.toSequence("ap"));
		assertMatches(sequence, factory.toSequence("at"));
		assertMatches(sequence, factory.toSequence("ak"));

		assertNotMatches(sequence, factory.toSequence("aa"));
	}

	@Test
	void testMatches04() {
		Sequence<Integer> sequence = factory.toSequence("a[-cnt,+rel,-vce]");

		assertMatches(sequence, factory.toSequence("ap͜f"));
		assertMatches(sequence, factory.toSequence("at͜s"));
		assertMatches(sequence, factory.toSequence("ak͜x"));

		assertNotMatches(sequence, factory.toSequence("ab͜v"));
		assertNotMatches(sequence, factory.toSequence("ad͜z"));
		assertNotMatches(sequence, factory.toSequence("ag͡ɣ"));

		assertNotMatches(sequence, factory.toSequence("ap"));
		assertNotMatches(sequence, factory.toSequence("at"));
		assertNotMatches(sequence, factory.toSequence("ak"));

		assertNotMatches(sequence, factory.toSequence("ab"));
		assertNotMatches(sequence, factory.toSequence("ad"));
		assertNotMatches(sequence, factory.toSequence("ag"));

		assertNotMatches(sequence, factory.toSequence("aa"));
	}

	@Test
	void testMatchesEmptyModel() {

		SequenceFactory<Integer> emptyFactory = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> sequence = emptyFactory.toSequence("an");
		assertMatches(sequence, emptyFactory.toSequence("an"));
		assertNotMatches(sequence, emptyFactory.toSequence("aa"));
	}

	@Test
	void testReverse() {
		Sequence<Integer> sequence1 = factory.toSequence("foo");
		Sequence<Integer> sequence2 = factory.toSequence("oof");
		Sequence<Integer> sequence3 = factory.toSequence("nan");

		assertEquals(sequence1, sequence2.getReverseSequence());
		assertEquals(sequence2, sequence1.getReverseSequence());
		assertEquals(sequence3, sequence3.getReverseSequence());
	}

	@Test
	void testGet() {
		Sequence<Integer> received = factory.toSequence("Sequences");

		assertEquals(factory.toSegment("S"), received.get(0));
		assertEquals(factory.toSegment("e"), received.get(1));
		assertEquals(factory.toSegment("q"), received.get(2));
		assertEquals(factory.toSegment("s"), received.get(8));
	}

	@Test
	void testAddSequence() {
		Sequence<Integer> received = factory.toSequence("Sequ");
		Sequence<Integer> addition = factory.toSequence("ence");
		Sequence<Integer> expected = factory.toSequence("Sequence");

		received.add(addition);

		assertEquals(expected, received);
	}

	@Test
	void testAddSequenceWrongModel() {

		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> received = factory.toSequence("Sequ");
		Sequence<Integer> addition = factory1.toSequence("ence");

		assertThrows(IllegalArgumentException.class, () -> received.add(addition));
	}

	@Test
	void testContains() {

		Sequence<Integer> sequence1 = factory.toSequence("Sequence");
		Sequence<Integer> sequence2 = factory.toSequence("ence");
		Sequence<Integer> sequence3 = factory.toSequence("a");

		assertTrue(sequence1.contains(sequence2));
		assertFalse(sequence1.contains(sequence3));
	}

	@Test
	void testContainsWrongModel() {

		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> sequence1 = factory.toSequence("Sequence");
		Sequence<Integer> sequence2 = factory1.toSequence("ence");

		assertFalse(sequence1.contains(sequence2));
	}

	@Test
	void testStartsWith() {

		Sequence<Integer> sequence1 = factory.toSequence("Sequence");
		Sequence<Integer> sequence2 = factory.toSequence("Seq");
		Sequence<Integer> sequence3 = factory.toSequence("a");

		assertTrue(sequence1.startsWith(sequence2));
		assertFalse(sequence1.startsWith(sequence3));
	}

	@Test
	void testStartsWithSegment() {

		Sequence<Integer> sequence = factory.toSequence("Sequence");
		Segment<Integer> segment1 = factory.toSegment("S");
		Segment<Integer> segment2 = factory.toSegment("a");

		assertTrue(sequence.startsWith(segment1));
		assertFalse(sequence.startsWith(segment2));
	}

	@Test
	void testStartsWithWrongModel() {

		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> sequence1 = factory.toSequence("Sequence");
		Sequence<Integer> sequence2 = factory1.toSequence("Seq");

		assertFalse(sequence1.startsWith(sequence2));
	}

	@Test
	void testStartsWithSegmentWrongModel() {

		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> sequence = factory.toSequence("Sequence");
		Segment<Integer> segment = factory1.toSegment("S");

		assertFalse(sequence.startsWith(segment));
	}

	@Test
	void testIndexOfSequenceWrongModel() {

		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				IntegerFeature.INSTANCE.emptyLoader().getFeatureMapping(),
				FormatterMode.NONE
		);

		Sequence<Integer> sequence1 = factory.toSequence("Sequence");
		Sequence<Integer> sequence2 = factory1.toSequence("ence");

		assertEquals(-1, sequence1.indexOf(sequence2));
	}

	@Test
	void testAddArray() {

		Sequence<Integer> received = factory.toSequence("a");
		received.add(factory.toSegment("w"));
		received.add(factory.toSegment("o"));
		received.add(factory.toSegment("r"));
		received.add(factory.toSegment("d"));

		Sequence<Integer> expected = factory.toSequence("aword");

		assertEquals(expected, received);
	}

	@Test
	void testEquals01() {
		assertEquals("sardo", "sardo");

		assertNotEquals("sardo", "sardox");
		assertNotEquals("sardo", "sārdo");
		assertNotEquals("sardo", "saardo");
		assertNotEquals("sardo", "sōrdo");
		assertNotEquals("sardo", "serdox");
		assertNotEquals("sardo", "ʃɛʁʔð");
	}

	@Test
	void testEquals02() {
		assertEquals("pʰāḱʰus", "pʰāḱʰus");
		assertNotEquals("pʰāḱʰus", "bāḱʰus");
	}

	@Test
	void testSubsequence01() {
		Sequence<Integer> sequence = factory.toSequence("expiated");
		Sequence<Integer> expected = factory.toSequence("iated");
		Sequence<Integer> received = sequence.subsequence(3);
		assertEquals(expected, received);
	}

	@Test
	void testSubsequence02() {
		Sequence<Integer> sequence = factory.toSequence("expiated");
		Sequence<Integer> expected = factory.toSequence("iat");
		Sequence<Integer> received = sequence.subsequence(3, 6);
		assertEquals(expected, received);
	}

	@Test
	void testSubsequence03() {
		Sequence<Integer> sequence = factory.toSequence("expiated");
		Sequence<Integer> expected = factory.toSequence("xpiat");
		Sequence<Integer> received = sequence.subsequence(1, 6);
		assertEquals(expected, received);
	}

	@Test
	void testIndexOf01() {
		Sequence<Integer> sequence = factory.toSequence("expiated");

		assertIndexOf("e", sequence, 0);
		assertIndexOf("ex", sequence, 0);
		assertIndexOf("exp", sequence, 0);
		assertIndexOf("expi", sequence, 0);
		assertIndexOf("expia", sequence, 0);
		assertIndexOf("expiat", sequence, 0);
		assertIndexOf("expiate", sequence, 0);
		assertIndexOf("expiated", sequence, 0);
		assertIndexOf("expiatedddd", sequence, -1);

		assertIndexOf("x", sequence, 1);
		assertIndexOf("xp", sequence, 1);
		assertIndexOf("xpi", sequence, 1);
		assertIndexOf("xpia", sequence, 1);
		assertIndexOf("xpiat", sequence, 1);
		assertIndexOf("xpiate", sequence, 1);
		assertIndexOf("xpiated", sequence, 1);

		assertIndexOf("p", sequence, 2);
		assertIndexOf("pi", sequence, 2);
		assertIndexOf("pia", sequence, 2);
		assertIndexOf("piat", sequence, 2);
		assertIndexOf("piate", sequence, 2);
		assertIndexOf("piated", sequence, 2);

		assertIndexOf("i", sequence, 3);
		assertIndexOf("ia", sequence, 3);
		assertIndexOf("iat", sequence, 3);
		assertIndexOf("iate", sequence, 3);
		assertIndexOf("iated", sequence, 3);

		assertIndexOf("a", sequence, 4);
		assertIndexOf("at", sequence, 4);
		assertIndexOf("ate", sequence, 4);
		assertIndexOf("ated", sequence, 4);

		assertIndexOf("t", sequence, 5);
		assertIndexOf("te", sequence, 5);
		assertIndexOf("ted", sequence, 5);

		assertIndexOf("d", sequence, 7);
	}

	@Test
	void testIndexOf02() {
		Sequence<Integer> sequence = factory.toSequence("subverterunt");

		assertEquals(-1,  sequence.indexOf(factory.toSequence("s"), 2));
		assertEquals( 0,  sequence.indexOf(factory.toSequence("s"), 0));
		assertEquals( 4,  sequence.indexOf(factory.toSequence("er"), 4));
		assertEquals( 7,  sequence.indexOf(factory.toSequence("er"), 7));
		assertEquals( 11, sequence.indexOf(factory.toSequence("t"), 7));
		assertEquals( 7,  sequence.indexOf(factory.toSequence("eru"), 0));
	}

	@Test
	void testIndexOfEmpty() {

		Sequence<Integer> sequence = factory.toSequence("subverterunt");

		assertEquals(-1,  sequence.indexOf(factory.toSequence("s"), 2));
		assertEquals( 0,  sequence.indexOf(factory.toSequence("s"), 0));
		assertEquals( 4,  sequence.indexOf(factory.toSequence("er"), 4));
		assertEquals( 7,  sequence.indexOf(factory.toSequence("er"), 7));
		assertEquals( 11, sequence.indexOf(factory.toSequence("t"), 7));
	}

	@Test
	void testIndices03() {
		Sequence<Integer> sequence = factory.toSequence("subverterunt");

		Collection<Integer> expected = new ArrayList<>();
		expected.add(4);
		expected.add(7);

		List<Integer> received = sequence.indicesOf(factory.toSequence("er"));

		assertEquals(expected, received);
	}

	@Test
	void testIndices04() {
		Sequence<Integer> sequence = factory.toSequence("aonaontada");

		Collection<Integer> expected = new ArrayList<>();
		expected.add(0);
		expected.add(3);
		List<Integer> received = sequence.indicesOf(factory.toSequence("ao"));

		assertEquals(expected, received);
	}

	@Test
	void testRemove01() {
		Sequence<Integer> sequence = factory.toSequence("abcdefghijk");
		Sequence<Integer> expected = factory.toSequence("cdefghijk");

		Sequence<Integer> received = new PhoneticSequence<>(sequence);
		Sequence<Integer> removed = received.remove(0, 2);

		assertEquals(expected, received);
		assertEquals(removed, factory.toSequence("ab"));
	}

	@Test
	void testRemove02() {
		Sequence<Integer> sequence = factory.toSequence("abcdefghijk");
		Sequence<Integer> expected = factory.toSequence("defghijk");

		Sequence<Integer> received = new PhoneticSequence<>(sequence);
		Sequence<Integer> removed = received.remove(0, 3);

		assertEquals(expected, received);
		assertEquals(removed, factory.toSequence("abc"));
	}

	@Test
	void testRemove03() {
		Sequence<Integer> sequence = factory.toSequence("abcdefghijk");
		Sequence<Integer> expected = factory.toSequence("adefghijk");

		Sequence<Integer> received = new PhoneticSequence<>(sequence);
		Sequence<Integer> removed = received.remove(1, 3);

		assertEquals(expected, received);
		assertEquals(removed, factory.toSequence("bc"));
	}

	@Test
	void testRemove04() {
		Sequence<Integer> sequence = factory.toSequence("abcdefghijk");
		Sequence<Integer> expected = factory.toSequence("abcghijk");

		Sequence<Integer> received = new PhoneticSequence<>(sequence);
		Sequence<Integer> removed = received.remove(3, 6);

		assertEquals(expected, received);
		assertEquals(removed, factory.toSequence("def"));
	}

	@Test
	void testRemove05() {
		Sequence<Integer> sequence = factory.toSequence("abcdefghijk");
		Sequence<Integer> expected = factory.toSequence("abcdhijk");

		Sequence<Integer> received = new PhoneticSequence<>(sequence);
		Sequence<Integer> removed = received.remove(4, 7);

		assertEquals(expected, received);
		assertEquals(removed, factory.toSequence("efg"));
	}

	@Test
	void testReplaceAllSequences01() {
		Sequence<Integer> sequence = factory.toSequence("aoSaontada");
		Sequence<Integer> expected = factory.toSequence("ouSountada");
		Sequence<Integer> received = sequence.replaceAll(factory.toSequence("ao"),
				factory.toSequence("ou"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences02() {
		Sequence<Integer> sequence = factory.toSequence("farcical");
		Sequence<Integer> expected = factory.toSequence("faarcicaal");
		Sequence<Integer> received = sequence.replaceAll(factory.toSequence("a"),
				factory.toSequence("aa"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences03() {
		Sequence<Integer> sequence = factory.toSequence("farcical");
		Sequence<Integer> expected = factory.toSequence("faearcicaeal");
		Sequence<Integer> received = sequence.replaceAll(factory.toSequence("a"),
				factory.toSequence("aea"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences04() {
		Sequence<Integer> sequence = factory.toSequence("onomotopaea");
		Sequence<Integer> expected = factory.toSequence("ɔɤnɔɤmɔɤtɔɤpaea");
		Sequence<Integer> received = sequence.replaceAll(factory.toSequence("o"),
				factory.toSequence("ɔɤ"));

		assertEquals(expected, received);
	}

	@Test
	void testStartsWith01() {
		Sequence<Integer> sequence = factory.toSequence("tekton");

		assertTrue(sequence.startsWith(factory.toSequence("tekton")));
		assertTrue(sequence.startsWith(factory.toSequence("tekto")));
		assertTrue(sequence.startsWith(factory.toSequence("tekt")));
		assertTrue(sequence.startsWith(factory.toSequence("tek")));
		assertTrue(sequence.startsWith(factory.toSequence("te")));
	}

	@Test
	void testStartsWith02() {
		Sequence<Integer> sequence = factory.toSequence("ton");

		assertTrue(sequence.startsWith(factory.toSequence("t")));
		assertTrue(sequence.startsWith(factory.toSequence("to")));
		assertTrue(sequence.startsWith(factory.toSequence("ton")));

		assertNotStartsWith(sequence, "tons");
		assertNotStartsWith(sequence, "tekton");
		assertNotStartsWith(sequence, "tekto");
		assertNotStartsWith(sequence, "tekt");
		assertNotStartsWith(sequence, "tek");
		assertNotStartsWith(sequence, "te");
	}

	@Test
	void testStartsWith03() {
		Sequence<Integer> sequence = factory.toSequence("elə");

		assertFalse(sequence.startsWith(factory.toSequence("eʔo")));
	}

	private static void assertIndexOf(String e, Sequence<Integer> q, int i) {
		assertEquals(i, q.indexOf(factory.toSequence(e)));
	}

	private static void assertNotStartsWith(Sequence<Integer> sequence, String string) {
		Sequence<Integer> testSequence = factory.toSequence(string);
		assertFalse(sequence.startsWith(testSequence));
	}

	private static void assertStartsWith(Sequence<Integer> sequence, String string) {
		Sequence<Integer> testSequence = factory.toSequence(string);
		assertTrue(sequence.startsWith(testSequence));
	}

	private static <T> void assertMatches(Sequence<T> exp, Sequence<T> act) {
		boolean matches = exp.matches(act);
		assertTrue(matches, "'" + exp + "' does not match " + act);
	}

	private static <T> void assertNotMatches(Sequence<T> exp, Sequence<T> act) {
		assertFalse(exp.matches(act), "'" + exp + "' should not match " + act);
	}
}

