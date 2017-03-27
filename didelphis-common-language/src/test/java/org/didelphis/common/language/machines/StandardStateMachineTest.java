/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.common.language.machines;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.enums.ParseDirection;
import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.language.machines.sequences.SequenceMatcher;
import org.didelphis.common.language.machines.sequences.SequenceParser;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/14/2015
 */
public class StandardStateMachineTest {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(
			StandardStateMachineTest.class);

	private static final SequenceFactory<Double> FACTORY = factory();
	private static final int TIMEOUT = 2;

	private static SequenceFactory<Double> factory() {
		return new SequenceFactory<>(
				DoubleFeatureMapping.getEmpty(),
				FormatterMode.NONE);
	}

	private static void test(StateMachine<Sequence<Double>> machine, String target) {
		
		final Collection<Integer> matchIndices = new ArrayList<>();
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5),() -> {
			Collection<Integer> collection = testMachine(machine, target);
			matchIndices.addAll(collection);
		});
		Assertions.assertFalse(matchIndices.isEmpty(), "Machine failed to accept input: " + target);
	}

	@Test
	void testIllegalBoundary01() {
		testIllegal("a#?");
	}

	@Test
	void testIllegalBoundary02() {
		testIllegal("a#+");
	}

	@Test
	void testIllegalBoundary03() {
		testIllegal("a#*");
	}

	@Test
	void testIllegalBoundary04() {
		testIllegal("#*a");
	}

	@Test
	void testStarQuestionMark() {
		testIllegal("a*?");
	}

	@Test
	void testPlusQuestionMark() {
		testIllegal("a+?");
	}

	@Test
	void testPlusStarMark() {
		testIllegal("a+*");
	}

	@Test
	void testUnmatchedCurly() {
		testIllegal("{a");
	}

	@Test
	void testUnmatchedParen() {
		testIllegal("(a");
	}


	@Test
	void testBasic01() {
		StateMachine<Sequence<Double>> machine = getMachine("a");

		test(machine, "a");
		test(machine, "aa");

		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<Sequence<Double>> machine = getMachine("aaa");

		test(machine, "aaa");

		fail(machine, "a");
		fail(machine, "aa");
		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	void testBasic03() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("aaa?");

		test(machine, "aa");
		test(machine, "aaa");

		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	void testBasic04() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("ab*cd?ab");

		test(machine, "acab");
		test(machine, "abcab");
		test(machine, "abbcab");
		test(machine, "abbbcab");

		test(machine, "acdab");
		test(machine, "abcdab");
		test(machine, "abbcdab");
		test(machine, "abbbcdab");

		fail(machine, "acddab");
		fail(machine, "abcddab");
		fail(machine, "abbcddab");
		fail(machine, "abbbcddab");
	}

	@Test
	void testStar() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("aa*");

		test(machine, "a");
		test(machine, "aa");
		test(machine, "aaa");
		test(machine, "aaaa");
		test(machine, "aaaaa");
		test(machine, "aaaaaa");
	}

	@Test
	void testStateMachinePlus() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("a+");

		test(machine, "a");
		test(machine, "aa");
		test(machine, "aaa");
		test(machine, "aaaa");
		test(machine, "aaaaa");
		test(machine, "aaaaaa");

		test(machine, "ab");
	}

	@Test
	void testGroups() {
		StateMachine<Sequence<Double>> machine = getMachine("(ab)(cd)(ef)");

		test(machine, "abcdef");
		fail(machine, "abcd");
		fail(machine, "ab");
		fail(machine, "bcdef");
	}

	@Test
	void testGroupStar01() {
		StateMachine<Sequence<Double>> machine = getMachine("(ab)*(cd)(ef)");

		test(machine, "abababcdef");
		test(machine, "ababcdef");
		test(machine, "abcdef");
		test(machine, "cdef");

		fail(machine, "abcd");
		fail(machine, "ab");
		fail(machine, "bcdef");
		fail(machine, "abbcdef");
	}

	@Test
	void testGroupStar02() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("d(eo*)*b");

		test(machine, "db");
		test(machine, "deb");
		test(machine, "deeb");
		test(machine, "deob");
		test(machine, "deoob");
		test(machine, "deoeob");
		test(machine, "deoeoob");

		fail(machine, "abcd");
		fail(machine, "ab");
		fail(machine, "bcdef");
		fail(machine, "abbcdef");
	}

	@Test
	void testGroupOptional01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("(ab)?(cd)(ef)");

		test(machine, "abcdef");
		test(machine, "cdef");
	}


	@Test
	void testSets01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("{ x ɣ }");

		test(machine, "x");
		test(machine, "ɣ");
		fail(machine, " ");
	}

	@Test
	void testSets02() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("{ab {cd xy} ef}tr");

		test(machine, "abtr");
		test(machine, "cdtr");
		test(machine, "xytr");
		test(machine, "eftr");
		fail(machine, " ");
	}

	@Test
	void testSetsExtraSpace01() {
		StateMachine<Sequence<Double>> machine = getMachine("{cʰ  c  ɟ}");

		test(machine, "cʰ");
		test(machine, "c");
		test(machine, "ɟ");
	}

	@Test
	void testGroupPlus01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("(ab)+");

		test(machine, "ab");
		test(machine, "abab");
		test(machine, "ababab");
	}

	@Test
	void testComplexGroups01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("(a+l(ham+b)*ra)+");

		test(machine, "alhambra");
	}

	@Test
	void testComplex06() {
		StateMachine<Sequence<Double>> machine = getMachine("{r l}{i u}s");

		test(machine, "ris");
		test(machine, "rus");

		test(machine, "lis");
		test(machine, "lus");

		fail(machine, "is");
		fail(machine, "us");

		fail(machine, "rs");
		fail(machine, "ls");
	}

	@Test
	void testComplex07() {
		StateMachine<Sequence<Double>> machine = getMachine("{r l}?{i u}?s");

		test(machine, "s");

		test(machine, "is");
		test(machine, "us");
		
		test(machine, "rs");
		test(machine, "ls");

		test(machine, "ris");
		test(machine, "rus");

		test(machine, "lis");
		test(machine, "lus");
	}

	@Test
	void testComplex02() {
		StateMachine<Sequence<Double>> machine = getMachine(
				"{r l}?{a e o ā ē ō}{i u}?{n m l r}?{pʰ tʰ kʰ cʰ}us");

		test(machine, "ācʰus");
	}

	@Test
	void testComplex03() {
		StateMachine<Sequence<Double>> machine = getMachine("a?{pʰ tʰ kʰ cʰ}us");

		test(machine, "pʰus");
		test(machine, "tʰus");
		test(machine, "kʰus");
		test(machine, "cʰus");
		test(machine, "acʰus");
	}

	@Test
	void testComplex04() {
		StateMachine<Sequence<Double>> machine = getMachine(
				"{a e o ā ē ō}{pʰ tʰ kʰ cʰ}us");
		
		test(machine, "apʰus");
		test(machine, "atʰus");
		test(machine, "akʰus");
		test(machine, "acʰus");
	}

	@Test
	void testComplex01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("a?(b?c?)d?b");

		test(machine, "b");
		test(machine, "db");
		test(machine, "bcdb");
		test(machine, "acdb");
		test(machine, "abdb");
		test(machine, "abcb");
		test(machine, "abcdb");
	}

	@Test
	void testComplex05() {
		StateMachine<Sequence<Double>> machine = getMachine(
				"{ab* (cd?)+ ((ae)*f)+}tr");

		test(machine, "abtr");
		test(machine, "cdtr");
		test(machine, "ftr");
		test(machine, "aeftr");
		test(machine, "aeaeftr");

		test(machine, "cctr");
		test(machine, "ccctr");
		test(machine, "fftr");
		test(machine, "aefaeftr");
		test(machine, "aefffffaeftr");

		fail(machine, "abcd");
		fail(machine, "tr");
	}

	@Test
	void testDot01() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("..");

		test(machine, "ab");
		test(machine, "db");
		test(machine, "bcdb");
		test(machine, "acdb");
		test(machine, "abdb");
		test(machine, "abcb");
		test(machine, "abcdb");

		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");
		fail(machine, "d");
		fail(machine, "e");
		fail(machine, "");
	}

	@Test
	void testDot02() throws IOException {
		StateMachine<Sequence<Double>> machine = getMachine("a..");

		test(machine, "abb");
		test(machine, "acdb");
		test(machine, "abdb");
		test(machine, "abcb");
		test(machine, "abcdb");

		fail(machine, "");
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");
		fail(machine, "d");
		fail(machine, "e");
		fail(machine, "aa");
		fail(machine, "db");
		fail(machine, "bcdb");
	}

	@Test
	void testGroupsDot() {
		StateMachine<Sequence<Double>> machine = getMachine(".*(cd)(ef)");

		test(machine, "cdef");
		test(machine, "bcdef");
		test(machine, "abcdef");
		test(machine, "xabcdef");
		test(machine, "xyabcdef");

		fail(machine, "abcd");
		fail(machine, "ab");
	}

	@Test
	void testGroupsDotPlus() {
		StateMachine<Sequence<Double>> machine = getMachine(".+(cd)(ef)");

		test(machine, "bcdef");
		test(machine, "abcdef");
		test(machine, "xabcdef");
		test(machine, "xyabcdef");

		fail(machine, "cdef");
		fail(machine, "abcd");
		fail(machine, "ab");
	}


	@Test
	void testBoundary() {
		StateMachine<Sequence<Double>> machine = getMachine("a#");

		test(machine, "a");

		fail(machine, "ab");
	}


	@Test
	void testGroupsDotStar() {
		StateMachine<Sequence<Double>> machine = getMachine("(a.)*cd#");

		test(machine, "cd");
		test(machine, "aXcd");
		test(machine, "aXaYcd");
		test(machine, "aXaYaZcd");

		fail(machine, "cdef");
		fail(machine, "bcd");
		fail(machine, "acd");
	}

	private static void testIllegal(String expression) {
		assertThrows(ParseException.class, () -> getMachine(expression));
	}

	private static StateMachine<Sequence<Double>> getMachine(String expression) {
		SequenceParser<Double> parser = new SequenceParser<>(FACTORY);
		SequenceMatcher<Double> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create("M0",
				expression,
				parser,
				matcher,
				ParseDirection.FORWARD);
	}

	private static void fail(StateMachine<Sequence<Double>> machine,
			String target) {
		final Collection<Integer> matchIndices = new ArrayList<>();
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(TIMEOUT),() -> {
			Collection<Integer> collection = testMachine(machine, target);
			matchIndices.addAll(collection);
		});
		Assertions.assertTrue(matchIndices.isEmpty(),
				"Machine accepted input it should not have: " + target);
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Double>> machine, String target) {
		Sequence<Double> sequence = FACTORY.getSequence(target);
		return machine.getMatchIndices(0, sequence);
	}
}
