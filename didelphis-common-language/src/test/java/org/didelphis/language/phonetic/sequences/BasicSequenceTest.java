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

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasicSequenceTest extends PhoneticTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(
			BasicSequenceTest.class);
	@Test
	void testMatches01() {
		Sequence<Integer> sequence = factory.getSequence("an");
		assertMatches(sequence, factory.getSequence("an"));
		assertNotMatches(sequence, factory.getSequence("aa"));
	}

	@Test
	void testMatches02() {
		Sequence<Integer> sequence = factory.getSequence("a");

		assertMatches(sequence, factory.getSequence("a"));
		assertNotMatches(sequence, factory.getSequence("an"));
		assertNotMatches(sequence, factory.getSequence("n"));
		assertNotMatches(sequence, factory.getSequence("b"));
		assertNotMatches(sequence, factory.getSequence("e"));
		assertNotMatches(sequence, factory.getSequence("c"));
	}

	@Test
	void testMatches03() throws IOException {
		FormatterMode mode = FormatterMode.INTELLIGENT;
		String name = "AT_hybrid.model";

		FeatureMapping<Integer> mapping =  new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				name).getFeatureMapping();

		SequenceFactory<Integer> factory = new SequenceFactory<>(mapping, mode);

		Sequence<Integer> sequence = factory.getSequence("a[-continuant, -son]");

		assertMatches(sequence, factory.getSequence("ap"));
		assertMatches(sequence, factory.getSequence("at"));
		assertMatches(sequence, factory.getSequence("ak"));

		assertNotMatches(sequence, factory.getSequence("aa"));
	}

	@Test
	void testMatches04() throws IOException {
		FormatterMode mode = FormatterMode.INTELLIGENT;

		String name = "AT_hybrid.model";

		FeatureMapping<Integer> mapping =  new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				name).getFeatureMapping();
		SequenceFactory<Integer> factory = new SequenceFactory<>(mapping, mode);

		Sequence<Integer> sequence = factory.getSequence("a[-cnt,+rel,-vce]");

		assertMatches(sequence, factory.getSequence("apf"));
		assertMatches(sequence, factory.getSequence("ats"));
		assertMatches(sequence, factory.getSequence("akx"));

		assertNotMatches(sequence, factory.getSequence("abv"));
		assertNotMatches(sequence, factory.getSequence("adz"));
		assertNotMatches(sequence, factory.getSequence("agɣ"));

		assertNotMatches(sequence, factory.getSequence("ap"));
		assertNotMatches(sequence, factory.getSequence("at"));
		assertNotMatches(sequence, factory.getSequence("ak"));

		assertNotMatches(sequence, factory.getSequence("ab"));
		assertNotMatches(sequence, factory.getSequence("ad"));
		assertNotMatches(sequence, factory.getSequence("ag"));

		assertNotMatches(sequence, factory.getSequence("aa"));
	}

	@Test
	void testGet() {
		Sequence<Integer> received = factory.getSequence("Sequences");

		assertEquals(factory.getSegment("S"), received.get(0));
		assertEquals(factory.getSegment("e"), received.get(1));
		assertEquals(factory.getSegment("q"), received.get(2));
		assertEquals(factory.getSegment("s"), received.get(8));
		assertEquals(factory.getSegment("S"), received.get(0));
		assertEquals(factory.getSegment("s"), received.get(received.size()-1));
	}

	@Test
	void testAddSequence() {
		Sequence<Integer> received = factory.getSequence("Sequ");
		Sequence<Integer> addition = factory.getSequence("ence");
		Sequence<Integer> expected = factory.getSequence("Sequence");

		received.add(addition);

		assertEquals(expected, received);
	}

	@Test
	void testAddArray() {

		Sequence<Integer> received = factory.getSequence("a");
		received.add(factory.getSegment("w"));
		received.add(factory.getSegment("o"));
		received.add(factory.getSegment("r"));
		received.add(factory.getSegment("d"));

		Sequence<Integer> expected = factory.getSequence("aword");

		assertEquals(expected, received);
	}

	@Test
	void testEquals01() {
		assertEquals("sardo", "sardo");

		assertNotEqual("sardo", "sardox");
		assertNotEqual("sardo", "sārdo");
		assertNotEqual("sardo", "saardo");
		assertNotEqual("sardo", "sōrdo");
		assertNotEqual("sardo", "serdox");
		assertNotEqual("sardo", "ʃɛʁʔð");
	}

	@Test
	void testEquals02() {
		assertEquals("pʰāḱʰus", "pʰāḱʰus");
		assertNotEqual("pʰāḱʰus", "bāḱʰus");
	}

	@Test
	void testSubsequence01() {
		Sequence<Integer> sequence = factory.getSequence("expiated");
		Sequence<Integer> expected = factory.getSequence("iated");
		Sequence<Integer> received = sequence.subsequence(3);
		assertEquals(expected, received);
	}

	@Test
	void testSubsequence02() {
		Sequence<Integer> sequence = factory.getSequence("expiated");
		Sequence<Integer> expected = factory.getSequence("iat");
		Sequence<Integer> received = sequence.subsequence(3, 6);
		assertEquals(expected, received);
	}

	@Test
	void testSubsequence03() {
		Sequence<Integer> sequence = factory.getSequence("expiated");
		Sequence<Integer> expected = factory.getSequence("xpiat");
		Sequence<Integer> received = sequence.subsequence(1, 6);
		assertEquals(expected, received);
	}

	@Test
	void testIndexOf01() {
		Sequence<Integer> sequence = factory.getSequence("expiated");

		assertIndexOf("e", sequence, 0);
		assertIndexOf("ex", sequence, 0);
		assertIndexOf("exp", sequence, 0);
		assertIndexOf("expi", sequence, 0);
		assertIndexOf("expia", sequence, 0);
		assertIndexOf("expiat", sequence, 0);
		assertIndexOf("expiate", sequence, 0);
		assertIndexOf("expiated", sequence, 0);

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

		FeatureModelLoader<Integer> loader = IntegerFeature.emptyLoader();
		SequenceFactory<Integer> factory = new SequenceFactory<>(loader.getFeatureMapping(), FormatterMode.NONE);

		Sequence<Integer> sequence = factory.getSequence("subverterunt");

		assertEqual(-1, sequence.indexOf(factory.getSequence("s"), 2));
		assertEqual(0, sequence.indexOf(factory.getSequence("s"), 0));
		assertEqual(4, sequence.indexOf(factory.getSequence("er"), 4));
		assertEqual(7, sequence.indexOf(factory.getSequence("er"), 7));
		assertEqual(11, sequence.indexOf(factory.getSequence("t"), 7));
	}

	@Test
	void testIndices03() {
		FeatureModelLoader<Integer> loader = IntegerFeature.emptyLoader();
		SequenceFactory<Integer> factory = new SequenceFactory<>(loader.getFeatureMapping(), FormatterMode.NONE);

		Sequence<Integer> sequence = factory.getSequence("subverterunt");

		Collection<Integer> expected = new ArrayList<>();
		expected.add(4);
		expected.add(7);

		List<Integer> received = sequence.indicesOf(factory.getSequence("er"));

		assertEquals(expected, received);
	}

	@Test
	void testIndices04() {
		Sequence<Integer> sequence = factory.getSequence("aonaontada");

		Collection<Integer> expected = new ArrayList<>();
		expected.add(0);
		expected.add(3);
		List<Integer> received = sequence.indicesOf(factory.getSequence("ao"));

		assertEquals(expected, received);
	}

	@Test
	void testRemove01() {
		Sequence<Integer> sequence = factory.getSequence("abcdefghijk");
		Sequence<Integer> expected = factory.getSequence("cdefghijk");

		Sequence<Integer> received = new BasicSequence<>(sequence);
		Sequence<Integer> removed = received.remove(0, 2);

		assertEquals(expected, received);
		assertEquals(removed, factory.getSequence("ab"));
	}

	@Test
	void testRemove02() {
		Sequence<Integer> sequence = factory.getSequence("abcdefghijk");
		Sequence<Integer> expected = factory.getSequence("defghijk");

		Sequence<Integer> received = new BasicSequence<>(sequence);
		Sequence<Integer> removed = received.remove(0, 3);

		assertEquals(expected, received);
		assertEquals(removed, factory.getSequence("abc"));
	}

	@Test
	void testRemove03() {
		Sequence<Integer> sequence = factory.getSequence("abcdefghijk");
		Sequence<Integer> expected = factory.getSequence("adefghijk");

		Sequence<Integer> received = new BasicSequence<>(sequence);
		Sequence<Integer> removed = received.remove(1, 3);

		assertEquals(expected, received);
		assertEquals(removed, factory.getSequence("bc"));
	}

	@Test
	void testRemove04() {
		Sequence<Integer> sequence = factory.getSequence("abcdefghijk");
		Sequence<Integer> expected = factory.getSequence("abcghijk");

		Sequence<Integer> received = new BasicSequence<>(sequence);
		Sequence<Integer> removed = received.remove(3, 6);

		assertEquals(expected, received);
		assertEquals(removed, factory.getSequence("def"));
	}

	@Test
	void testRemove05() {
		Sequence<Integer> sequence = factory.getSequence("abcdefghijk");
		Sequence<Integer> expected = factory.getSequence("abcdhijk");

		Sequence<Integer> received = new BasicSequence<>(sequence);
		Sequence<Integer> removed = received.remove(4, 7);

		assertEquals(expected, received);
		assertEquals(removed, factory.getSequence("efg"));
	}

	@Test
	void testReplaceAllSequences01() {
		Sequence<Integer> sequence = factory.getSequence("aoSaontada");
		Sequence<Integer> expected = factory.getSequence("ouSountada");
		Sequence<Integer> received = sequence.replaceAll(factory.getSequence("ao"),
				factory.getSequence("ou"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences02() {
		Sequence<Integer> sequence = factory.getSequence("farcical");
		Sequence<Integer> expected = factory.getSequence("faarcicaal");
		Sequence<Integer> received = sequence.replaceAll(factory.getSequence("a"),
				factory.getSequence("aa"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences03() {
		Sequence<Integer> sequence = factory.getSequence("farcical");
		Sequence<Integer> expected = factory.getSequence("faearcicaeal");
		Sequence<Integer> received = sequence.replaceAll(factory.getSequence("a"),
				factory.getSequence("aea"));

		assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences04() {
		Sequence<Integer> sequence = factory.getSequence("onomotopaea");
		Sequence<Integer> expected = factory.getSequence("ɔɤnɔɤmɔɤtɔɤpaea");
		Sequence<Integer> received = sequence.replaceAll(factory.getSequence("o"),
				factory.getSequence("ɔɤ"));

		assertEquals(expected, received);
	}

	@Test
	void testStartsWith01() {
		Sequence<Integer> sequence = factory.getSequence("tekton");

		assertTrue(sequence.startsWith(factory.getSequence("tekton")));
		assertTrue(sequence.startsWith(factory.getSequence("tekto")));
		assertTrue(sequence.startsWith(factory.getSequence("tekt")));
		assertTrue(sequence.startsWith(factory.getSequence("tek")));
		assertTrue(sequence.startsWith(factory.getSequence("te")));
	}

	@Test
	void testStartsWith02() {
		Sequence<Integer> sequence = factory.getSequence("ton");

		assertTrue(sequence.startsWith(factory.getSequence("t")));
		assertTrue(sequence.startsWith(factory.getSequence("to")));
		assertTrue(sequence.startsWith(factory.getSequence("ton")));

		assertNotStartsWith(sequence, "tons");
		assertNotStartsWith(sequence, "tekton");
		assertNotStartsWith(sequence, "tekto");
		assertNotStartsWith(sequence, "tekt");
		assertNotStartsWith(sequence, "tek");
		assertNotStartsWith(sequence, "te");
	}

	@Test
	void testStartsWith03() {
		Sequence<Integer> sequence = factory.getSequence("elə");

		assertFalse(sequence.startsWith(factory.getSequence("eʔo")));
	}

	private static void assertIndexOf(String e, Sequence<Integer> q, int i) {
		assertEqual(i, q.indexOf(factory.getSequence(e)));
	}

	private static void assertNotStartsWith(Sequence<Integer> sequence, String string) {
		Sequence<Integer> testSequence = factory.getSequence(string);
		assertFalse(sequence.startsWith(testSequence));
	}

	private static void assertStartsWith(Sequence<Integer> sequence, String string) {
		Sequence<Integer> testSequence = factory.getSequence(string);
		assertTrue(sequence.startsWith(testSequence));
	}

	private static void assertEqual(int expected, int actual) {
		assertEquals(expected, actual);
	}

	private static void assertNotEqual(Object unexpected, Object actual) {
		assertFalse(unexpected.equals(actual));
	}

	private static <T> void assertMatches(Sequence<T> exp, Sequence<T> act) {
		boolean matches = exp.matches(act);
		assertTrue(matches, "\'" + exp + "\' does not match " + act);
	}

	private static <T> void assertNotMatches(Sequence<T> exp, Sequence<T> act) {
		assertFalse(exp.matches(act),
				"\'" + exp + "\' should not match " + act);
	}
}

