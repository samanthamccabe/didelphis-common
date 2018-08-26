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
import org.didelphis.language.automata.matching.SequenceMatcher;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 * @date 3/14/2015
 */
class StandardStateMachineTest {

	private static final Logger LOG = Logger.create(StandardStateMachineTest.class);

	private static final Duration DURATION = Duration.ofSeconds(1);
	private static final boolean TIMEOUT = false;
	
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
		
		assertMatches(machine, "a");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBoundaries2() {
		StateMachine<Sequence<Integer>> machine = getMachine("#a");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		
		assertNotMatches(machine, "");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "x");
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
	void testCapturingGroups() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)(cd)(ef)");

		assertMatchesGroup(machine, "abcdef", 0, "abcdef");
		assertMatchesGroup(machine, "abcdef", 1, "ab");
		assertMatchesGroup(machine, "abcdef", 2, "cd");
		assertMatchesGroup(machine, "abcdef", 3, "ef");

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
		assertTimeoutPreemptively(DURATION, ()->{
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
	void testComplex08() {
		StateMachine<Sequence<Integer>> machine = getMachine("{r #}{i u}?s");

		assertMatches(machine, "s");

		assertMatches(machine, "is");
		assertMatches(machine, "us");

		assertMatches(machine, "rs");
		assertMatches(machine, "ls");

		assertMatches(machine, "ris");
		assertMatches(machine, "rus");

		assertNotMatches(machine, "lis");
		assertNotMatches(machine, "lus");
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
		assertTimeoutPreemptively(DURATION, () ->{

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

	@Test
	void testSpecials01() {

		MultiMap<String, Sequence<Integer>> multiMap = new GeneralMultiMap<>();
		multiMap.add("CH", FACTORY.toSequence("ph"));
		multiMap.add("CH", FACTORY.toSequence("th"));
		multiMap.add("CH", FACTORY.toSequence("kh"));
		
		StateMachine<Sequence<Integer>> machine = getMachine("aCHa", multiMap);

		assertMatches(machine, "apha");
		assertMatches(machine, "atha");
		assertMatches(machine, "akha");

		assertNotMatches(machine, "aCHa");
		assertNotMatches(machine, "apa");
		assertNotMatches(machine, "ata");
		assertNotMatches(machine, "aka");
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
		Executable executable = () -> {
			Collection<Integer> collection = testMachine(machine, target);
			Collection<Integer> matchIndices = new ArrayList<>(collection);
			assertFalse(
					matchIndices.isEmpty(),
					"Machine failed to accept input: " + target
			);
		};
		
		if (TIMEOUT) {
			assertTimeoutPreemptively(DURATION, executable);
		} else {
			try {
				executable.execute();
			} catch (Throwable throwable) {
				LOG.error("Unexpected failure encountered: {}", throwable);
			}
		}
	}
	
	private static void assertThrowsParse(String expression) {
		assertThrows(ParseException.class, () -> getMachine(expression));
	}

	private static StateMachine<Sequence<Integer>> getMachine(String expression) {
		return getMachine(expression, null);
	}

	private static StateMachine<Sequence<Integer>> getMachine(
			String exp,
			MultiMap<String, Sequence<Integer>> specials
	) {
		SequenceParser<Integer> parser = specials == null 
				? new SequenceParser<>(FACTORY)
				: new SequenceParser<>(FACTORY, specials);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		Expression expression = parser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0",
				expression,
				parser,
				matcher);
	}

	private static void assertNotMatches(StateMachine<Sequence<Integer>> machine, String target) {
		Executable executable = () -> {
			Collection<Integer> matchIndices = testMachine(machine, target);
			assertTrue(
					matchIndices.isEmpty(),
					"Machine accepted input it should not have: " + target
			);
		};
		
		if (TIMEOUT) {
			assertTimeoutPreemptively(DURATION, executable);
		} else {
			try {
				executable.execute();
			} catch (Throwable throwable) {
				LOG.error("Unexpected failure encountered: {}", throwable);
			}
		}
	}

	private static void assertMatchesGroup(
			StateMachine<Sequence<Integer>> machine,
			String input,
			int group,
			String expected
	) {
		Sequence<Integer> sequence = FACTORY.toSequence(input);
		Match<Sequence<Integer>> match = machine.match(sequence, 0);
		Sequence<Integer> matchedGroup = match.group(group);
		assertEquals(FACTORY.toSequence(expected), matchedGroup);
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Integer>> machine, String target) {
		Sequence<Integer> sequence = FACTORY.toSequence(target);
		Match<Sequence<Integer>> match = machine.match(sequence, 0);
		return match.end() >= 0
				? Collections.singleton(match.end())
				: Collections.emptyList();
	}
}
