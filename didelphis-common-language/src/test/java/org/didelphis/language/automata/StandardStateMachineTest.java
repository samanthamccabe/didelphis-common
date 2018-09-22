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
import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Samantha Fiona McCabe
 */
class StandardStateMachineTest extends StateMachineTestBase<Sequence<Integer>> {

	private static final SequenceFactory<Integer> FACTORY = factory();
	private static SequenceParser<Integer> parser;

	@Override
	protected Sequence<Integer> transform(String input) {
		return input.isEmpty()
				? FACTORY.toSequence(input)
				: parser.transform(input);
	}
	
	@Test
	void testSplit01() {
		StateMachine<Sequence<Integer>> machine = getMachine("");
		Sequence<Integer> sequence = factory().toSequence("abc");
		List<Sequence<Integer>> split = machine.split(sequence);
		assertEquals(3, split.size());
	}

	@Test
	void testSplit02() {
		StateMachine<Sequence<Integer>> machine = getMachine("b");
		Sequence<Integer> sequence = factory().toSequence("abc");
		List<Sequence<Integer>> split = machine.split(sequence);
		assertEquals(2, split.size());
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
	void testCapturingGroups01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)(cd)(ef)");

		assertMatchesGroup(machine, "abcdef", "abcdef", 0);
		assertMatchesGroup(machine, "abcdef", "ab", 1);
		assertMatchesGroup(machine, "abcdef", "cd", 2);
		assertMatchesGroup(machine, "abcdef", "ef", 3);
	}

	@Test
	void testCapturingGroups02() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)(?:cd)(ef)");

		assertMatchesGroup(machine, "abcdef", "abcdef", 0);
		assertMatchesGroup(machine, "abcdef", "ab", 1);
		assertMatchesGroup(machine, "abcdef", "ef", 2);
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
	}

	@Test
	void testGroupOptional01() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)?(cd)(ef)");

		assertMatches(machine, "abcdef");
		assertMatches(machine, "cdef");
	}

	@Test
	void testCapturingGroupsOptional() {
		StateMachine<Sequence<Integer>> machine = getMachine("(ab)?(cd)(ef)");

		assertMatchesGroup(machine, "abcdef", "abcdef", 0);
		assertMatchesGroup(machine, "abcdef", "ab", 1);
		assertMatchesGroup(machine, "abcdef", "cd", 2);
		assertMatchesGroup(machine, "abcdef", "ef", 3);

		assertMatchesGroup(machine, "cdef", "cdef", 0);
		assertMatchesGroup(machine, "cdef", "cd", 2);
		assertMatchesGroup(machine, "cdef", "ef", 3);

		assertNoGroup(machine, "cdef", 1);
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
		String exp = "{ab {cd xy} ef}tr";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

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
		String exp = "(a+l(ham+b)*ra)+";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "alhambra");
	}

	@Test
	void testComplexCapturingGroups01() {
		String exp = "(a+l(ham+b)*ra)+";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatchesGroup(machine, "alhambraalhambra", "alhambraalhambra", 0);
		
		assertMatchesGroup(machine, "alhambra", "alhambra", 0);
		assertMatchesGroup(machine, "alhambra", "alhambra", 1);
		assertMatchesGroup(machine, "alhambra", "hamb", 2);

		assertMatchesGroup(machine, "aalhammbhambra", "aalhammbhambra", 0);
		assertMatchesGroup(machine, "aalhammbhambra", "aalhammbhambra", 1);
		assertMatchesGroup(machine, "aalhammbhambra", "hammbhamb", 2);
		
		assertMatchesGroup(machine, "alra", "alra", 0);
		assertMatchesGroup(machine, "alra", "alra", 1);
		assertNoGroup(machine, "alra", 2);
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

		assertMatches(machine, "ris");
		assertMatches(machine, "rus");

		assertNotMatches(machine, "ls");
		assertNotMatches(machine, "lis");
		assertNotMatches(machine, "lus");
	}

	@Test
	void testComplex02() {
		String exp = "{r l}?{a e o ā ē ō}{i u}?{n m l r}?{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "ācʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex03() {
		String expression = "a?{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(expression);

		assertMatches(machine, "pʰus");
		assertMatches(machine, "tʰus");
		assertMatches(machine, "kʰus");
		assertMatches(machine, "cʰus");
		assertMatches(machine, "acʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex04() {
		String exp = "{a e o ā ē ō}{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);
		
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
	void testComplexCapture01() {
		StateMachine<Sequence<Integer>> machine = getMachine("a?(b?c?)d?b");

		assertMatchesGroup(machine, "b", "b", 0);
		assertNoGroup(machine, "b", 1);

		assertMatchesGroup(machine, "db", "db", 0);
		assertNoGroup(machine, "db", 1);

		assertMatchesGroup(machine, "bcdb", "bcdb", 0);
		assertMatchesGroup(machine, "bcdb", "bc", 1);

		assertMatchesGroup(machine, "acdb", "acdb", 0);
		assertMatchesGroup(machine, "acdb", "c", 1);
		
		assertMatchesGroup(machine, "abdb", "abdb", 0);
		assertMatchesGroup(machine, "abdb", "b", 1);

		assertMatchesGroup(machine, "ab", "ab", 0);
		assertNoGroup(machine, "ab", 1);
	}

	@Test
	void testComplex05() {

		String exp = "{ab* (cd?)+ ((ae)*f)+}tr";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

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
	}
	
	@Test
	void testComplexCaptureGroup() {

		String exp = "{ab* (cd?)+ ((ae)*f)+}tr";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatchesGroup(machine, "abtr", "abtr", 0);
		assertNoGroup(machine, "abtr", 1);
		assertNoGroup(machine, "abtr", 2);
		assertNoGroup(machine, "abtr", 3);

		assertMatchesGroup(machine, "cdtr", "cdtr", 0);
		assertMatchesGroup(machine, "cdtr", "cd", 1);
		assertNoGroup(machine, "cdtr", 2);
		assertNoGroup(machine, "cdtr", 3);

		assertMatchesGroup(machine, "ctr", "ctr", 0);
		assertMatchesGroup(machine, "ctr", "c", 1);

		assertMatchesGroup(machine, "ftr", "ftr", 0);
		assertMatchesGroup(machine, "ftr", "f", 2);
		
		assertNoGroup(machine, "ftr", 1);
		assertNoGroup(machine, "ftr", 3);

		assertMatchesGroup(machine, "aeftr", "aeftr", 0);
		assertMatchesGroup(machine, "aeftr", "aef", 2);
		assertNoGroup(machine, "aeftr", 1);

		assertMatchesGroup(machine, "aeaeftr", "aeaeftr", 0);
		assertMatchesGroup(machine, "aeaeftr", "aeaef", 2);
		assertMatchesGroup(machine, "aeaeftr", "aeae", 3);
		assertNoGroup(machine, "aeaeftr", 1);
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
		return new SequenceFactory<>(mapping, FormatterMode.INTELLIGENT);
	}

	private static StateMachine<Sequence<Integer>> getMachine(String expression) {
		return getMachine(expression, null);
	}

	private static StateMachine<Sequence<Integer>> getMachine(
			String exp,
			MultiMap<String, Sequence<Integer>> specials
	) {
		parser = specials == null 
				? new SequenceParser<>(FACTORY)
				: new SequenceParser<>(FACTORY, specials);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		Expression expression = parser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0",
				expression, parser,
				matcher);
	}
}
