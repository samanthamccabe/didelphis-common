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

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.matching.RegexMatcher;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.utilities.Logger;
import org.didelphis.utilities.Templates;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 */
class StandardStateMachineRegexTest extends StateMachineTestBase<String> {

	private static final Logger LOG = Logger.create(StandardStateMachineRegexTest.class);
	
	private static final RegexParser PARSER = new RegexParser();
	private static final RegexMatcher MATCHER = new RegexMatcher();
	public static final String CONSIST_MESSAGE = "Java regex returned {} but Didelphis regex returned {}";

	@Override
	protected String transform(String input) {
		return PARSER.transform(input);
	}

	@Test
	void testEmpty() {
		StateMachine<String> machine = getMachine("");
		assertMatches(machine, "");
		assertMatches(machine, "a");
		assertMatches(machine, "ab");
		assertMatches(machine, "abc");
	}
	
	@Test
	void testBoundaries1() {
		StateMachine<String> machine = getMachine("^a$");
		
		assertMatches(machine, "a");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBoundaries2() {
		StateMachine<String> machine = getMachine("^a");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		
		assertNotMatches(machine, "");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "x");
	}
	
	@Test
	void testBoundaries3() {
		StateMachine<String> machine = getMachine("a$");

		assertMatches(machine, "a");

		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBasic01() {
		StateMachine<String> machine = getMachine("a");
		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<String> machine = getMachine("aaa");
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
		StateMachine<String> machine = getMachine("aaa?");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasic04() {
		StateMachine<String> machine = getMachine("ab*cd?ab");

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
		StateMachine<String> machine = getMachine("aa*");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		assertMatches(machine, "aaaa");
		assertMatches(machine, "aaaaa");
		assertMatches(machine, "aaaaaa");
	}

	@Test
	void testStateMachinePlus() {
		StateMachine<String> machine = getMachine("a+");

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
		StateMachine<String> machine = getMachine("(ab)(cd)(ef)");

		assertMatchesGroup(machine, "abcdef", "abcdef", 0);
		assertMatchesGroup(machine, "abcdef", "ab", 1);
		assertMatchesGroup(machine, "abcdef", "cd", 2);
		assertMatchesGroup(machine, "abcdef", "ef", 3);
	}

	@Test
	void testCapturingGroups02() {
		StateMachine<String> machine = getMachine("(ab)(?:cd)(ef)");

		assertMatchesGroup(machine, "abcdef", "abcdef", 0);
		assertMatchesGroup(machine, "abcdef", "ab", 1);
		assertMatchesGroup(machine, "abcdef", "ef", 2);
	}

	@Test
	void testGroups() {
		StateMachine<String> machine = getMachine("(ab)(cd)(ef)");

		assertMatches(machine, "abcdef");
		assertNotMatches(machine, "abcd");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "bcdef");
	}

	@Test
	void testGroupStar01() {
		StateMachine<String> machine = getMachine("(ab)*(cd)(ef)");

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
		StateMachine<String> machine = getMachine("d(eo*)*b");

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
		StateMachine<String> machine = getMachine("(ab)?(cd)(ef)");

		assertMatches(machine, "abcdef");
		assertMatches(machine, "cdef");
	}

	@Test
	void testCapturingGroupsOptional() {
		StateMachine<String> machine = getMachine("(ab)?(cd)(ef)");

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
		StateMachine<String> machine = getMachine("[xɣ]");

		assertMatches(machine, "x");
		assertMatches(machine, "ɣ");
		assertNotMatches(machine, " ");
		assertNotMatches(machine, "a");
	}

	@Test
	void testSets02() {
		StateMachine<String> machine = getMachine("[(ab)(cd)(xy)(ef)]tr");

		assertMatches(machine, "abtr");
		assertMatches(machine, "cdtr");
		assertMatches(machine, "xytr");
		assertMatches(machine, "eftr");
		assertNotMatches(machine, " ");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "tr");
	}
	
	@Test
	void testSetsMultiCharacter() {
		StateMachine<String> machine = getMachine("[cɟ]");

		assertMatches(machine, "ɟ");
		assertMatches(machine, "c");
		assertNotMatches(machine, "a");
	}
	
	@Test
	void testNestedSquareBrackets() {
		StateMachine<String> machine = getMachine("[ab[cd]]");

		assertMatches(machine, "a");
		assertMatches(machine, "b");
		assertMatches(machine, "c");
		assertMatches(machine, "d");
		
		assertNotMatches(machine, "x");
	}

	@Test
	void testGroupPlus01() {
		StateMachine<String> machine = getMachine("(ab)+");

		assertMatches(machine, "ab");
		assertMatches(machine, "abab");
		assertMatches(machine, "ababab");
	}

	@Test
	void testGroupStarEnd01() {
		StateMachine<String> machine = getMachine("(ab)*$");

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
		StateMachine<String> machine = getMachine("(a+l(ham+b)*ra)+");

		assertMatches(machine, "alhambra");
	}

	@Test
	void testComplexCapturingGroups01() {
		StateMachine<String> machine = getMachine("(a+l(ham+b)*ra)+");

		assertMatchesGroup(machine, "alhambraalhambra", "alhambraalhambra", 0);
		
		assertMatchesGroup(machine, "alhambra", "alhambra", 0);
		assertMatchesGroup(machine, "alhambra", "alhambra", 1);
		assertMatchesGroup(machine, "alhambra", "hamb", 2);

		assertMatchesGroup(machine, "aalhammbhambra", "aalhammbhambra", 0);
		assertMatchesGroup(machine, "aalhammbhambra", "aalhammbhambra", 1);
		assertMatchesGroup(machine, "aalhammbhambra", "hammb", 2);
		
		assertMatchesGroup(machine, "alra", "alra", 0);
		assertMatchesGroup(machine, "alra", "alra", 1);
		assertNoGroup(machine, "alra", 2);
	}

	@Test
	void testComplex06() {
		StateMachine<String> machine = getMachine("[rl][iu]s");

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
		Pattern pattern = Pattern.compile("[rl]?[iu]?s");
		StateMachine<String> machine = getMachine("[rl]?[iu]?s");

		assertConsistant(pattern, machine, "s");

		assertConsistant(pattern, machine, "is");
		assertConsistant(pattern, machine, "us");
		
		assertConsistant(pattern, machine, "rs");
		assertConsistant(pattern, machine, "ls");

		assertConsistant(pattern, machine, "ris");
		assertConsistant(pattern, machine, "rus");

		assertConsistant(pattern, machine, "lis");
		assertConsistant(pattern, machine, "lus");
	}

	@Test
	void testComplex02() {
		StateMachine<String> machine = getMachine(
				"[rl]?[aeoāēō][iu]?[nmlr]?[ptkc]us");

		assertMatches(machine, "ācus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex03() {
		StateMachine<String> machine = getMachine("a?[ptkc]us");

		assertMatches(machine, "pus");
		assertMatches(machine, "tus");
		assertMatches(machine, "kus");
		assertMatches(machine, "cus");
		assertMatches(machine, "acus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex04() {
		StateMachine<String> machine = getMachine(
				"[aeoāēō][ptkc]us");
		
		assertMatches(machine, "apus");
		assertMatches(machine, "atus");
		assertMatches(machine, "akus");
		assertMatches(machine, "acus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex01() {
		StateMachine<String> machine = getMachine("a?(b?c?)d?b");

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
		StateMachine<String> machine = getMachine("a?(b?c?)d?b");

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
	void testDot01() {
		StateMachine<String> machine = getMachine("..");

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
		StateMachine<String> machine = getMachine("a..");

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
		StateMachine<String> machine = getMachine(".*(cd)(ef)");

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
		StateMachine<String> machine = getMachine(".+(cd)(ef)");

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
		StateMachine<String> machine = getMachine("a$");
		assertMatches(machine, "a");
		assertNotMatches(machine, "ab");
	}

	@Test
	void testGroupsDotStar() {
		StateMachine<String> machine = getMachine("(a.)*cd$");

		assertMatches(machine, "cd");
		assertMatches(machine, "aXcd");
		assertMatches(machine, "aXaYcd");
		assertMatches(machine, "aXaYaZcd");

		assertNotMatches(machine, "cdef");
		assertNotMatches(machine, "bcd");
		assertNotMatches(machine, "acd");
	}

	@Test
	void testWordCharacters() {
		String regex = "\\w+";
		Pattern pattern = Pattern.compile(regex);
		StateMachine<String> machine = getMachine(regex);

		assertConsistant(pattern, machine, "c");
		assertConsistant(pattern, machine, "cc");
		assertConsistant(pattern, machine, "cD");
		assertConsistant(pattern, machine, "cD0");
		assertConsistant(pattern, machine, "cD_0");
		assertConsistant(pattern, machine, "cD_09_a");
	}

	@Test
	void testConsistantNegative01() {
		String regex = "[^abc]";
		Pattern pattern = Pattern.compile(regex);
		StateMachine<String> machine = getMachine(regex);
		
		assertConsistant(pattern, machine, "a");
		assertConsistant(pattern, machine, "b");
		assertConsistant(pattern, machine, "c");
		assertConsistant(pattern, machine, "d");
	}

	@Test
	void testConsistantNegative02() {
		String regex = "^[^abc].[^abc]$";
		Pattern pattern = Pattern.compile(regex);
		StateMachine<String> machine = getMachine(regex);

		assertConsistant(pattern, machine, "aaa");
		assertConsistant(pattern, machine, "bac");
		assertConsistant(pattern, machine, "cab");
		assertConsistant(pattern, machine, "ddd");
		assertConsistant(pattern, machine, "d");
		assertConsistant(pattern, machine, "x");
		assertConsistant(pattern, machine, "dxdx");
		assertConsistant(pattern, machine, "dx");
	}

	@Test
	void testNestedNegative01() {
		@Language ("RegExp") 
		String regex = "^[^abc[^d]]$";
		StateMachine<String> machine = getMachine(regex);

		assertMatches(machine, "x");
		assertMatches(machine, "d");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testNestedNegative02() {
		String regex = "^[^abc[d]]$";
		Pattern pattern = Pattern.compile(regex);
		StateMachine<String> machine = getMachine(regex);

		assertMatches(machine, "x");

		assertConsistant(pattern, machine, "a");
		assertConsistant(pattern, machine, "b");
		assertConsistant(pattern, machine, "c");
		assertConsistant(pattern, machine, "d");
		
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
		
		// This is not quite expected, but the previous consistency check does
		// at least show that the behavior is the same as Pattern
		assertMatches(machine, "d");
	}

	@Test
	void testCase01() {
		StateMachine<String> machine = getMachine("^(a)?a");
		
		assertMatch(machine, "a",  2, "a", null);
		assertMatch(machine, "aa", 2, "aa", "a");
	}
	
	@Test
	void testCase02() {
		StateMachine<String> machine = getMachine("^(aa(bb)?)+$");
		assertMatch(machine, "aabb",    3, "aabb",   "aabb",   "bb");
		assertMatch(machine, "aaaa",    3, "aaaa",   "aa",   null);
		assertMatch(machine, "aabbaa",  3, "aabbaa", "aabb", "bb");

		assertFalse(machine.matches(""));
		assertFalse(machine.matches("bb"));
	}

	@Test
	void testCase03() {
		StateMachine<String> machine = getMachine("((a|b)?b)+");
		assertMatch(machine, "b",    3, "b",   "b",  null);
		assertMatch(machine, "bb",   3, "bb",  "b",  null);
		assertMatch(machine, "ab",   3, "ab",  "ab", "a");
		assertMatch(machine, "abbx", 3, "abb", "ab", "a");

		assertFalse(machine.matches(""));
		assertFalse(machine.matches("a"));
	}
	
	@Test
	void testCase04() {
		StateMachine<String> machine = getMachine("(aaa)?aaa");
		assertMatches(machine, "aaa");

		assertMatch(machine, "aaa",    2, "aaa",    null);
		assertMatch(machine, "aaaaaa", 2, "aaaaaa", "aaa");
		assertMatch(machine, "aaaa",   2, "aaa",    null);
		
		assertFalse(machine.matches(""));
		assertFalse(machine.matches("a"));
		assertFalse(machine.matches("aa"));
	}
	
	@Test
	void testCase05() {
		StateMachine<String> machine = getMachine("^(a(b)?)+$");
		assertMatch(machine, "aba",  3, "aba",  "ab", "b");
		assertMatch(machine, "abab", 3, "abab", "ab", "b");
		assertFalse(machine.matches("abb"));
	}

	@Test
	void testCase06() {
		StateMachine<String> machine = getMachine("^(a(b(c)?)?)?abc");
		assertMatch(machine, "abc",     4, "abc",    null,  null, null);
		assertMatch(machine, "abcabc",  4, "abcabc", "abc", "bc", "c");
	}

	@Test
	void testCase07() {
		StateMachine<String> machine = getMachine("^(a(b(c))).*");
		assertMatch(machine, "abcxx", 4, "abcxx", "abc",  "bc", "c");
	}

	@Test
	void testCase08() {
		assertMatches(getMachine("[abc]+[def]+[ghi]+"), "aaddggzzz");
	}

	@Test
	void testCase09() {
		StateMachine<String> machine = getMachine("[abc^b]");
		assertMatches(machine, "b");
		assertMatches(machine, "^");
	}

	@Test
	void testCase10() {
		StateMachine<String> machine = getMachine("[abc[def]]");
		assertMatches(machine, "b");
		assertMatches(machine, "e");
	}

	@Test
	void testCase11() {
		StateMachine<String> machine = getMachine("[a-d[0-9][m-p]]");
		assertMatches(machine, "a");
		assertMatches(machine, "o");
		assertMatches(machine, "4");
		assertNotMatches(machine, "e");
		assertNotMatches(machine, "u");
	}

	@Test
	void testCase12() {
		StateMachine<String> machine = getMachine("[a-c[d-f[g-i]]]");
		assertMatches(machine, "a");
		assertMatches(machine, "e");
		assertMatches(machine, "h");
		assertNotMatches(machine, "m");
	}

	@Test
	void testCase13() {
		StateMachine<String> machine = getMachine("[abc[def]ghi]");
		assertMatches(machine, "d");
		assertMatches(machine, "h");
		assertNotMatches(machine, "w");
		assertNotMatches(machine, "z");
	}

	@Test
	void testCase14() {
		StateMachine<String> machine = getMachine("[abc[^bcd]]");
		assertMatches(machine, "a");
		assertNotMatches(machine, "d");
	}

	@Test
	void testCase15() {
		StateMachine<String> machine = getMachine("ab\\wc");
		assertMatches(machine, "abcc");
	}

	@Test
	void testCase16() {
		StateMachine<String> machine = getMachine("\\W\\w\\W");
		assertMatches(machine, "#r#");
	}

	@Test
	void testCase17() {
		StateMachine<String> machine = getMachine("abc[\\sdef]*");
		assertMatches(machine, "abc  def");
	}

	@Test
	void testCase18() {
		StateMachine<String> machine = getMachine("abc[a-d\\sm-p]*");
		assertMatches(machine, "abcaa mn  p");
	}

	@Test
	void testCase19() {
		StateMachine<String> machine = getMachine("(a+b)+");
		assertMatches(machine, "ababab");
	}

	@Test
	void testCase20() {
		StateMachine<String> machine = getMachine("(a|b)+");
		assertMatches(machine, "ababab");
		assertNotMatches(machine, "cccccd");
	}

	@Test
	void testSplit01() {
		StateMachine<String> machine = getMachine("a");

		assertEquals(Arrays.asList("b", "b", "b"), machine.split("babab"));
		assertEquals(Arrays.asList("", "b", "b", ""), machine.split("ababa"));
		assertEquals(Arrays.asList("b", "b", ""), machine.split("baba"));
		assertEquals(Arrays.asList("", "b", "b"), machine.split("abab"));
	}
	
	@Test
	void testReplace01() {
		StateMachine<String> machine = getMachine("a");

		assertEquals("b,b,b",  machine.replace("babab", ","));
		assertEquals(",b,b,",  machine.replace("ababa", ","));
		assertEquals("b,b,",   machine.replace("baba",  ","));
		assertEquals(",b,b",   machine.replace("abab",  ","));
	}
	
	void assertConsistant(
			Pattern pattern,
			StateMachine<String> machine,
			String target
	) {
		Matcher matcher = pattern.matcher(target);
		int machineEnd = test(machine, target);
		int patternEnd = matcher.find() ? matcher.end() : -1;
		assertEquals(
				patternEnd,
				machineEnd, 
				Templates.compile(CONSIST_MESSAGE, patternEnd, machineEnd)
		);
	}

	private static void assertMatch(
			StateMachine<String> machine,
			String input,
			int groupCount,
			String... groups
	) {
		Match<String> match = machine.match(input);

		assertTrue(match.matches());
		assertEquals(groupCount, match.groupCount());
		for (int i = 0; i < groups.length; i++) {
			String expected = groups[i];
			String received = match.group(i);
			String message = "Group " + i + " was expected to match "
					+ expected + " but actually matched " + received;
			assertEquals(expected, received, message);
		}
	}

	static StateMachine<String> getMachine(@Language("RegExp") String exp) {
		RegexParser regexParser = new RegexParser();
		Expression expression = regexParser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0", expression, PARSER, MATCHER);
	}
}
