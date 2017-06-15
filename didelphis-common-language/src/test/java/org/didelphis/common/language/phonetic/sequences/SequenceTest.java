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

package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.features.IntegerFeature;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequenceTest {

	private static final Logger LOG = LoggerFactory.getLogger(
			SequenceTest.class);

	private static final SequenceFactory<Integer> FACTORY = new SequenceFactory<>(
			new FeatureModelLoader<>(
					IntegerFeature.INSTANCE,
					ClassPathFileHandler.INSTANCE,
					Collections.emptyList()).getFeatureMapping(),
			FormatterMode.INTELLIGENT);

	@Test
	void testMatches01() {
		Sequence<Integer> sequence = FACTORY.getSequence("an");

		assertMatches(sequence, FACTORY.getSequence("an"));

		assertNotMatches(sequence, FACTORY.getSequence("aa"));
	}

	@Test
	void testMatches02() {
		Sequence<Integer> sequence = FACTORY.getSequence("a");

		assertMatches(sequence, FACTORY.getSequence("a"));

		assertNotMatches(sequence, FACTORY.getSequence("an"));

		assertNotMatches(sequence, FACTORY.getSequence("n"));
		assertNotMatches(sequence, FACTORY.getSequence("b"));
		assertNotMatches(sequence, FACTORY.getSequence("e"));
		assertNotMatches(sequence, FACTORY.getSequence("c"));
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

		Sequence<Integer> sequence = factory.getSequence(
				"a[-continuant, +rel, -voice]");

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
		Sequence<Integer> received = FACTORY.getSequence("Sequences");

		Assertions.assertEquals(FACTORY.getSegment("S"), received.get(0));
		Assertions.assertEquals(FACTORY.getSegment("e"), received.get(1));
		Assertions.assertEquals(FACTORY.getSegment("q"), received.get(2));
		Assertions.assertEquals(FACTORY.getSegment("s"), received.get(8));
		Assertions.assertEquals(FACTORY.getSegment("S"), received.getFirst());
		Assertions.assertEquals(FACTORY.getSegment("s"), received.getLast());
	}

	@Test
	void testAddSequence() {
		Sequence<Integer> received = FACTORY.getSequence("Sequ");
		Sequence<Integer> addition = FACTORY.getSequence("ence");
		Sequence<Integer> expected = FACTORY.getSequence("Sequence");

		received.add(addition);

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testAddArray() {

		Sequence<Integer> received = FACTORY.getSequence("a");
		received.add(FACTORY.getSegment("w"));
		received.add(FACTORY.getSegment("o"));
		received.add(FACTORY.getSegment("r"));
		received.add(FACTORY.getSegment("d"));

		Sequence<Integer> expected = FACTORY.getSequence("aword");

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testEquals01() {
		Assertions.assertEquals("sardo", "sardo");

		assertNotEqual("sardo", "sardox");
		assertNotEqual("sardo", "sārdo");
		assertNotEqual("sardo", "saardo");
		assertNotEqual("sardo", "sōrdo");
		assertNotEqual("sardo", "serdox");
		assertNotEqual("sardo", "ʃɛʁʔð");
	}

	@Test
	void testEquals02() {
		Assertions.assertEquals("pʰāḱʰus", "pʰāḱʰus");
		assertNotEqual("pʰāḱʰus", "bāḱʰus");
	}

	@Test
	void testSubsequence01() {

		Sequence<Integer> sequence = FACTORY.getSequence("expiated");
		Sequence<Integer> expected = FACTORY.getSequence("iated");
		Sequence<Integer> received = sequence.subsequence(3);

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testSubsequence02() {

		Sequence<Integer> sequence = FACTORY.getSequence("expiated");
		Sequence<Integer> expected = FACTORY.getSequence("iat");
		Sequence<Integer> received = sequence.subsequence(3, 6);

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testSubsequence03() {

		Sequence<Integer> sequence = FACTORY.getSequence("expiated");
		Sequence<Integer> expected = FACTORY.getSequence("xpiat");
		Sequence<Integer> received = sequence.subsequence(1, 6);

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testIndexOf01() {
		Sequence<Integer> sequence = FACTORY.getSequence("expiated");

		testIndexOf("e", sequence, 0);
		testIndexOf("ex", sequence, 0);
		testIndexOf("exp", sequence, 0);
		testIndexOf("expi", sequence, 0);
		testIndexOf("expia", sequence, 0);
		testIndexOf("expiat", sequence, 0);
		testIndexOf("expiate", sequence, 0);
		testIndexOf("expiated", sequence, 0);

		testIndexOf("x", sequence, 1);
		testIndexOf("xp", sequence, 1);
		testIndexOf("xpi", sequence, 1);
		testIndexOf("xpia", sequence, 1);
		testIndexOf("xpiat", sequence, 1);
		testIndexOf("xpiate", sequence, 1);
		testIndexOf("xpiated", sequence, 1);

		testIndexOf("p", sequence, 2);
		testIndexOf("pi", sequence, 2);
		testIndexOf("pia", sequence, 2);
		testIndexOf("piat", sequence, 2);
		testIndexOf("piate", sequence, 2);
		testIndexOf("piated", sequence, 2);

		testIndexOf("i", sequence, 3);
		testIndexOf("ia", sequence, 3);
		testIndexOf("iat", sequence, 3);
		testIndexOf("iate", sequence, 3);
		testIndexOf("iated", sequence, 3);

		testIndexOf("a", sequence, 4);
		testIndexOf("at", sequence, 4);
		testIndexOf("ate", sequence, 4);
		testIndexOf("ated", sequence, 4);

		testIndexOf("t", sequence, 5);
		testIndexOf("te", sequence, 5);
		testIndexOf("ted", sequence, 5);

		testIndexOf("d", sequence, 7);
	}

	@Test
	void testIndexOf02() {
		//                                01234567
		Sequence<Integer> sequence = FACTORY.getSequence("subverterunt");

		assertEqual(-1, sequence.indexOf(FACTORY.getSequence("s"), 2));
		assertEqual(0, sequence.indexOf(FACTORY.getSequence("s"), 0));
		assertEqual(4, sequence.indexOf(FACTORY.getSequence("er"), 4));
		assertEqual(7, sequence.indexOf(FACTORY.getSequence("er"), 7));
		assertEqual(11, sequence.indexOf(FACTORY.getSequence("t"), 7));
	}

	@Test
	void testIndices03() {
		Sequence<Integer> sequence = FACTORY.getSequence("subverterunt");

		List<Integer> expected = new ArrayList<>();
		expected.add(4);
		expected.add(7);

		List<Integer> received = sequence.indicesOf(FACTORY.getSequence("er"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testIndices04() {
		Sequence<Integer> sequence = FACTORY.getSequence("aonaontada");

		List<Integer> expected = new ArrayList<>();
		expected.add(0);
		expected.add(3);
		List<Integer> received = sequence.indicesOf(FACTORY.getSequence("ao"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testRemove01() {
		Sequence<Integer> sequence = FACTORY.getSequence("abcdefghijk");
		Sequence<Integer> expected = FACTORY.getSequence("cdefghijk");

		//		Sequence<Integer> received = sequence.copy();
		Sequence<Integer> received = new BasicSequence(sequence);
		Sequence<Integer> removed = received.remove(0, 2);

		Assertions.assertEquals(expected, received);
		Assertions.assertEquals(removed, FACTORY.getSequence("ab"));
	}

	@Test
	void testRemove02() {
		Sequence<Integer> sequence = FACTORY.getSequence("abcdefghijk");
		Sequence<Integer> expected = FACTORY.getSequence("defghijk");

		Sequence<Integer> received = new BasicSequence(sequence);
		Sequence<Integer> removed = received.remove(0, 3);

		Assertions.assertEquals(expected, received);
		Assertions.assertEquals(removed, FACTORY.getSequence("abc"));
	}

	@Test
	void testRemove03() {
		Sequence<Integer> sequence = FACTORY.getSequence("abcdefghijk");
		Sequence<Integer> expected = FACTORY.getSequence("adefghijk");

		Sequence<Integer> received = new BasicSequence(sequence);
		Sequence<Integer> removed = received.remove(1, 3);

		Assertions.assertEquals(expected, received);
		Assertions.assertEquals(removed, FACTORY.getSequence("bc"));
	}

	@Test
	void testRemove04() {
		Sequence<Integer> sequence = FACTORY.getSequence("abcdefghijk");
		Sequence<Integer> expected = FACTORY.getSequence("abcghijk");

		Sequence<Integer> received = new BasicSequence(sequence);
		Sequence<Integer> removed = received.remove(3, 6);

		Assertions.assertEquals(expected, received);
		Assertions.assertEquals(removed, FACTORY.getSequence("def"));
	}

	@Test
	void testRemove05() {
		Sequence<Integer> sequence = FACTORY.getSequence("abcdefghijk");
		Sequence<Integer> expected = FACTORY.getSequence("abcdhijk");

		Sequence<Integer> received = new BasicSequence(sequence);
		Sequence<Integer> removed = received.remove(4, 7);

		Assertions.assertEquals(expected, received);
		Assertions.assertEquals(removed, FACTORY.getSequence("efg"));
	}

	@Test
	void testReplaceAllSequences01() {
		Sequence<Integer> sequence = FACTORY.getSequence("aoSaontada");
		Sequence<Integer> expected = FACTORY.getSequence("ouSountada");
		Sequence<Integer> received = sequence.replaceAll(FACTORY.getSequence("ao"),
				FACTORY.getSequence("ou"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences02() {
		Sequence<Integer> sequence = FACTORY.getSequence("farcical");
		Sequence<Integer> expected = FACTORY.getSequence("faarcicaal");
		Sequence<Integer> received = sequence.replaceAll(FACTORY.getSequence("a"),
				FACTORY.getSequence("aa"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences03() {
		Sequence<Integer> sequence = FACTORY.getSequence("farcical");
		Sequence<Integer> expected = FACTORY.getSequence("faearcicaeal");
		Sequence<Integer> received = sequence.replaceAll(FACTORY.getSequence("a"),
				FACTORY.getSequence("aea"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testReplaceAllSequences04() {
		Sequence<Integer> sequence = FACTORY.getSequence("onomotopaea");
		Sequence<Integer> expected = FACTORY.getSequence("ɔɤnɔɤmɔɤtɔɤpaea");
		Sequence<Integer> received = sequence.replaceAll(FACTORY.getSequence("o"),
				FACTORY.getSequence("ɔɤ"));

		Assertions.assertEquals(expected, received);
	}

	@Test
	void testStartsWith01() {
		Sequence<Integer> sequence = FACTORY.getSequence("tekton");

		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("tekton")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("tekto")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("tekt")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("tek")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("te")));
	}

	@Test
	void testStartsWith02() {
		Sequence<Integer> sequence = FACTORY.getSequence("ton");

		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("t")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("to")));
		Assertions.assertTrue(sequence.startsWith(FACTORY.getSequence("ton")));

		testNotStarstWith(sequence, "tons");
		testNotStarstWith(sequence, "tekton");
		testNotStarstWith(sequence, "tekto");
		testNotStarstWith(sequence, "tekt");
		testNotStarstWith(sequence, "tek");
		testNotStarstWith(sequence, "te");
	}

	@Test
	void testStartsWith03() {
		Sequence<Integer> sequence = FACTORY.getSequence("elə");

		Assertions.assertFalse(sequence.startsWith(FACTORY.getSequence("eʔo")));
	}

	private static void testIndexOf(String e, Sequence<Integer> sequence,
			int i) {
		assertEqual(i, sequence.indexOf(FACTORY.getSequence(e)));
	}

	private static void testNotStarstWith(Sequence<Integer> sequence,
			String testString) {
		boolean doesStartWith = isStartWith(sequence, testString);
		Assertions.assertFalse(doesStartWith);
	}

	private static boolean isStartWith(Sequence<Integer> sequence,
			String testString) {
		Sequence<Integer> testSequence = FACTORY.getSequence(testString);
		return sequence.startsWith(testSequence);
	}

	private static void assertEqual(int a, int b) {
		Assertions.assertEquals(a, b);
	}

	private static void assertNotEqual(Serializable a,
			Serializable b) {
		Assertions.assertFalse(a.equals(b));
	}

	private static void assertMatches(Sequence<Integer> left,
			Sequence<Integer> right) {
		Assertions.assertTrue(left.matches(right),
				"\'" + left + "\' does not match " + right);
	}

	private static void assertNotMatches(Sequence<Integer> left,
			Sequence<Integer> right) {
		Assertions.assertFalse(left.matches(right),
				"\'" + left + "\' should not match " + right);
	}
}

