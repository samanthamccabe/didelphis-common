/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
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

package org.didelphis.language.automata;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.interfaces.StateMachine;
import org.didelphis.language.automata.sequences.SequenceMatcher;
import org.didelphis.language.automata.sequences.SequenceParser;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 * @date 3/14/2015
 */
class StandardStateMachineTest {

	private static final SequenceFactory<Integer> FACTORY = factory();

	@Test
	void testIllegalBoundary01() {
		assertThrowsParse("a#?");
	}

	@Test
	void testIllegalBoundary02() {
		assertThrowsParse("a#+");
	}

	@Test
	void testIllegalBoundary03() {
		assertThrowsParse("a#*");
	}

	@Test
	void testIllegalBoundary04() {
		assertThrowsParse("#*a");
	}

	@Test
	void testStarQuestionMark() {
		assertThrowsParse("a*?");
	}

	@Test
	void testPlusQuestionMark() {
		assertThrowsParse("a+?");
	}

	@Test
	void testPlusStarMark() {
		assertThrowsParse("a+*");
	}

	@Test
	void testUnmatchedCurly() {
		assertThrowsParse("{a");
	}

	@Test
	void testUnmatchedParen() {
		assertThrowsParse("(a");
	}

	@Test
	void testEmpty() {
		StateMachine<Sequence<Integer>> machine = getMachine("");
		assertMatches(machine, "");
		assertMatches(machine, "a");
		assertMatches(machine, "ab");
		assertMatches(machine, "abc");
	}

	@Test
	void testBoundaryOnly() {
		StateMachine<Sequence<Integer>> machine = getMachine("#");
		assertMatches(machine, "");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "abc");
	}

	@Test
	void testBoundaries1() {
		StateMachine<Sequence<Integer>> machine = getMachine("#a#");
		
		assertMatches(machine, "#a#");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBoundaries2() {
		StateMachine<Sequence<Integer>> machine = getMachine("#a");

		assertMatches(machine, "#a");
		assertMatches(machine, "#aa");
		
		assertNotMatches(machine, "");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "x");

		assertNotMatches(machine, "a");
		assertNotMatches(machine, "#ba");
		assertNotMatches(machine, "#x");
	}
	
	@Test
	void testBoundaries3() {
		StateMachine<Sequence<Integer>> machine = getMachine("a#");

		assertMatches(machine, "a");

		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBasic01() {
		StateMachine<Sequence<Integer>> machine = getMachine("a");
		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<Sequence<Integer>> machine = getMachine("aaa");
		assertMatches(machine, "aaa");
		
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "bb");
		assertNotMatches(machine, "bbb");
		assertNotMatches(machine, "c");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "abb");
	}
	
	@Test
	void testBasic03() {
		StateMachine<Sequence<Integer>> machine = getMachine("aaa?");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasic04() {
		StateMachine<Sequence<Integer>> machine = getMachine("ab*cd?ab");

		assertMatches(machine, "acab");
		assertMatches(machine, "abcab");
		assertMatches(machine, "abbcab");
		assertMatches(machine, "abbbcab");

		assertMatches(machine, "acdab");
		assertMatches(machine, "abcdab");
		assertMatches(machine, "abbcdab");
		assertMatches(machine, "abbbcdab");

		assertNotMatches(machine, "acddab");
		assertNotMatches(machine, "abcddab");
		assertNotMatches(machine, "abbcddab");
		assertNotMatches(machine, "abbbcddab");

		assertNotMatches(machine, "a");
	}

	@Test
	void testStar() {
		StateMachine<Sequence<Integer>> machine = getMachine("aa*");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		assertMatches(machine, "aaaa");
		assertMatches(machine, "aaaaa");
		assertMatches(machine, "aaaaaa");
	}

	@Test
	void testStateMachinePlus() {
		StateMachine<Sequence<Integer>> machine = getMachine("a+");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		assertMatches(machine, "aaaa");
		assertMatches(machine, "aaaaa");
		assertMatches(machine, "aaaaaa");

		assertMatches(machine, "ab");
	}

	@Test
	void testGroups() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)(cd)(ef)");

		assertMatches(machine, "abcdef");
		assertNotMatches(machine, "abcd");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "bcdef");
	}

	@Test
	void testGroupStar01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)*(cd)(ef)");

		assertMatches(machine, "abababcdef");
		assertMatches(machine, "ababcdef");
		assertMatches(machine, "abcdef");
		assertMatches(machine, "cdef");

		assertNotMatches(machine, "abcd");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "bcdef");
		assertNotMatches(machine, "abbcdef");
	}

	@Test
	void testGroupStar02() {
		assertTimeoutPreemptively(Duration.ofSeconds(1), ()->{
			StateMachine<Sequence<Integer>> machine = getMachine("d(eo*)*b");

			assertMatches(machine, "db");
			assertMatches(machine, "deb");
			assertMatches(machine, "deeb");
			assertMatches(machine, "deob");
			assertMatches(machine, "deoob");
			assertMatches(machine, "deoeob");
			assertMatches(machine, "deoeoob");

			assertNotMatches(machine, "abcd");
			assertNotMatches(machine, "ab");
			assertNotMatches(machine, "bcdef");
			assertNotMatches(machine, "abbcdef");			
		});
	}

	@Test
	void testGroupOptional01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)?(cd)(ef)");

		assertMatches(machine, "abcdef");
		assertMatches(machine, "cdef");
	}

	@Test
	void testSets01() {
		StateMachine<Sequence<Integer>> machine = getMachine("{ x ɣ }");

		assertMatches(machine, "x");
		assertMatches(machine, "ɣ");
		assertNotMatches(machine, " ");
		assertNotMatches(machine, "a");
	}

	@Test
	void testSets02() {
		StateMachine<Sequence<Integer>> machine = getMachine("{ab {cd xy} ef}tr");

		assertMatches(machine, "abtr");
		assertMatches(machine, "cdtr");
		assertMatches(machine, "xytr");
		assertMatches(machine, "eftr");
		assertNotMatches(machine, " ");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "tr");
	}
	
	@Test
	void testSetsExtraSpace01() {
		StateMachine<Sequence<Integer>> machine = getMachine("{cʰ  c  ɟ}");

		assertMatches(machine, "ɟ");
		assertMatches(machine, "c");
		assertMatches(machine, "cʰ");
		assertNotMatches(machine, "a");
	}

	@Test
	void testGroupPlus01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)+");

		assertMatches(machine, "ab");
		assertMatches(machine, "abab");
		assertMatches(machine, "ababab");
	}

	@Test
	void testGroupStarEnd01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)*#");

		assertMatches(machine, "");
		assertMatches(machine, "ab");
		assertMatches(machine, "abab");
		assertMatches(machine, "ababab");

		assertNotMatches(machine, "a");
		assertNotMatches(machine, "aba");
		assertNotMatches(machine, "ababa");
		assertNotMatches(machine, "abababa");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "baba");
		assertNotMatches(machine, "bababa");
	}

	@Test
	void testComplexGroups01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(a+l(ham+b)*ra)+");

		assertMatches(machine, "alhambra");
	}

	@Test
	void testComplex06() {
		StateMachine<Sequence<Integer>> machine = getMachine("{r l}{i u}s");

		assertMatches(machine, "ris");
		assertMatches(machine, "rus");

		assertMatches(machine, "lis");
		assertMatches(machine, "lus");

		assertNotMatches(machine, "is");
		assertNotMatches(machine, "us");

		assertNotMatches(machine, "rs");
		assertNotMatches(machine, "ls");
	}
		
	@Test
	void testComplex07() {
		StateMachine<Sequence<Integer>> machine = getMachine("{r l}?{i u}?s");

		assertMatches(machine, "s");

		assertMatches(machine, "is");
		assertMatches(machine, "us");
		
		assertMatches(machine, "rs");
		assertMatches(machine, "ls");

		assertMatches(machine, "ris");
		assertMatches(machine, "rus");

		assertMatches(machine, "lis");
		assertMatches(machine, "lus");
	}

	@Test
	void testComplex02() {
		StateMachine<Sequence<Integer>> machine = getMachine(
				"{r l}?{a e o ā ē ō}{i u}?{n m l r}?{pʰ tʰ kʰ cʰ}us");

		assertMatches(machine, "ācʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex03() {
		StateMachine<Sequence<Integer>> machine = getMachine("a?{pʰ tʰ kʰ cʰ}us");

		assertMatches(machine, "pʰus");
		assertMatches(machine, "tʰus");
		assertMatches(machine, "kʰus");
		assertMatches(machine, "cʰus");
		assertMatches(machine, "acʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex04() {
		StateMachine<Sequence<Integer>> machine = getMachine(
				"{a e o ā ē ō}{pʰ tʰ kʰ cʰ}us");
		
		assertMatches(machine, "apʰus");
		assertMatches(machine, "atʰus");
		assertMatches(machine, "akʰus");
		assertMatches(machine, "acʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex01() {
		StateMachine<Sequence<Integer>> machine = getMachine("a?(b?c?)d?b");

		assertMatches(machine, "b");
		assertMatches(machine, "db");
		assertMatches(machine, "bcdb");
		assertMatches(machine, "acdb");
		assertMatches(machine, "abdb");
		assertMatches(machine, "abcb");
		assertMatches(machine, "abcdb");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex05() {
		assertTimeoutPreemptively(Duration.ofSeconds(1), () ->{

			StateMachine<Sequence<Integer>> machine = getMachine(
					"{ab* (cd?)+ ((ae)*f)+}tr");

			assertMatches(machine, "abtr");
			assertMatches(machine, "cdtr");
			assertMatches(machine, "ftr");
			assertMatches(machine, "aeftr");
			assertMatches(machine, "aeaeftr");

			assertMatches(machine, "cctr");
			assertMatches(machine, "ccctr");
			assertMatches(machine, "fftr");
			assertMatches(machine, "aefaeftr");
			assertMatches(machine, "aefffffaeftr");

			assertNotMatches(machine, "abcd");
			assertNotMatches(machine, "tr");

			assertNotMatches(machine, "a");
		} );
	}

	@Test
	void testDot01() {
		StateMachine<Sequence<Integer>> machine = getMachine("..");

		assertMatches(machine, "ab");
		assertMatches(machine, "db");
		assertMatches(machine, "bcdb");
		assertMatches(machine, "acdb");
		assertMatches(machine, "abdb");
		assertMatches(machine, "abcb");
		assertMatches(machine, "abcdb");

		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
		assertNotMatches(machine, "d");
		assertNotMatches(machine, "e");
		assertNotMatches(machine, "");
	}

	@Test
	void testDot02() {
		StateMachine<Sequence<Integer>> machine = getMachine("a..");

		assertMatches(machine, "abb");
		assertMatches(machine, "acdb");
		assertMatches(machine, "abdb");
		assertMatches(machine, "abcb");
		assertMatches(machine, "abcdb");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
		assertNotMatches(machine, "d");
		assertNotMatches(machine, "e");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "db");
		assertNotMatches(machine, "bcdb");
	}

	@Test
	void testGroupsDot() {
		StateMachine<Sequence<Integer>> machine = getMachine(".*(cd)(ef)");

		assertMatches(machine, "cdef");
		assertMatches(machine, "bcdef");
		assertMatches(machine, "abcdef");
		assertMatches(machine, "xabcdef");
		assertMatches(machine, "xyabcdef");

		assertNotMatches(machine, "abcd");
		assertNotMatches(machine, "ab");
	}

	@Test
	void testGroupsDotPlus() {
		StateMachine<Sequence<Integer>> machine = getMachine(".+(cd)(ef)");

		assertMatches(machine, "bcdef");
		assertMatches(machine, "abcdef");
		assertMatches(machine, "xabcdef");
		assertMatches(machine, "xyabcdef");

		assertNotMatches(machine, "cdef");
		assertNotMatches(machine, "abcd");
		assertNotMatches(machine, "ab");
	}

	@Test
	void testBoundary() {
		StateMachine<Sequence<Integer>> machine = getMachine("a#");
		assertMatches(machine, "a");
		assertNotMatches(machine, "ab");
	}

	@Test
	void testGroupsDotStar() {
		StateMachine<Sequence<Integer>> machine = getMachine("(a.)*cd#");

		assertMatches(machine, "cd");
		assertMatches(machine, "aXcd");
		assertMatches(machine, "aXaYcd");
		assertMatches(machine, "aXaYaZcd");

		assertNotMatches(machine, "cdef");
		assertNotMatches(machine, "bcd");
		assertNotMatches(machine, "acd");
	}

	@Test
	void testBoundaryInSet01() {
		StateMachine<Sequence<Integer>> machine = getMachine("m{a b #}");

		assertMatches(machine, "mb");
		assertMatches(machine, "ma");
		assertMatches(machine, "m");

		assertNotMatches(machine, "xa");
		assertNotMatches(machine, "xb");

		assertNotMatches(machine, "xaa");
		assertNotMatches(machine, "xab");
	}

	private static SequenceFactory<Integer> factory() {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				Collections.emptyList());
		FeatureMapping<Integer> mapping = loader.getFeatureMapping();
		return new SequenceFactory<>(mapping, FormatterMode.NONE);
	}

	private static void assertMatches(StateMachine<Sequence<Integer>> machine, String target) {
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			Collection<Integer> collection = testMachine(machine, target);
			Collection<Integer> matchIndices = new ArrayList<>(collection);
			assertFalse(
					matchIndices.isEmpty(),
					"Machine failed to accept input: " + target
			);
		});
	}
	
	private static void assertThrowsParse(String expression) {
		assertThrows(ParseException.class, () -> getMachine(expression));
	}

	private static StateMachine<Sequence<Integer>> getMachine(String expression) {
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		Expression parseExpression = parser.parseExpression(expression);
		return StandardStateMachine.create("M0",
				parseExpression,
				parser,
				matcher,
				ParseDirection.FORWARD);
	}

	private static void assertNotMatches(StateMachine<Sequence<Integer>> machine, String target) {
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			Collection<Integer> matchIndices = testMachine(machine, target);
			assertTrue(
					matchIndices.isEmpty(),
					"Machine accepted input it should not have: " + target
			);
		});
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Integer>> machine, String target) {
		FeatureModel<Integer> model = FACTORY.getFeatureMapping().getFeatureModel();
		Sequence<Integer> sequence = new BasicSequence<>(model);
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY);
		if (target.startsWith("#")) sequence.add(parser.transform("#["));
		sequence.add(FACTORY.toSequence(target.replaceAll("#","")));
		if (target.endsWith("#")) sequence.add(parser.transform("]#"));
		return machine.getMatchIndices(0, sequence);
	}
}
