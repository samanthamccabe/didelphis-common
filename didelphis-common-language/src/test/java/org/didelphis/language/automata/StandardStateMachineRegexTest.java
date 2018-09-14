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

import lombok.NonNull;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.matching.RegexMatcher;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.utilities.Logger;
import org.didelphis.utilities.Templates;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 */
class StandardStateMachineRegexTest {

	private static final Logger LOG = Logger.create(StandardStateMachineRegexTest.class);

	private static final Duration DURATION = Duration.ofSeconds(1);
	private static final boolean TIMEOUT = false;
	
	private static final RegexParser PARSER = new RegexParser();
	private static final RegexMatcher MATCHER = new RegexMatcher();
	public static final String CONSIST_MESSAGE = "Java regex returned {} but Didelphis regex returned {}";

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

		assertMatchesGroup(machine, "abcdef", 0, "abcdef");
		assertMatchesGroup(machine, "abcdef", 1, "ab");
		assertMatchesGroup(machine, "abcdef", 2, "cd");
		assertMatchesGroup(machine, "abcdef", 3, "ef");
	}

	@Test
	void testCapturingGroups02() {
		StateMachine<String> machine = getMachine("(ab)(?:cd)(ef)");

		assertMatchesGroup(machine, "abcdef", 0, "abcdef");
		assertMatchesGroup(machine, "abcdef", 1, "ab");
		assertMatchesGroup(machine, "abcdef", 2, "ef");
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

		assertMatchesGroup(machine, "abcdef", 0, "abcdef");
		assertMatchesGroup(machine, "abcdef", 1, "ab");
		assertMatchesGroup(machine, "abcdef", 2, "cd");
		assertMatchesGroup(machine, "abcdef", 3, "ef");

		assertMatchesGroup(machine, "cdef", 0, "cdef");
		assertMatchesGroup(machine, "cdef", 2, "cd");
		assertMatchesGroup(machine, "cdef", 3, "ef");

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

		assertMatchesGroup(machine, "alhambraalhambra", 0, "alhambraalhambra");
		
		assertMatchesGroup(machine, "alhambra", 0, "alhambra");
		assertMatchesGroup(machine, "alhambra", 1, "alhambra");
		assertMatchesGroup(machine, "alhambra", 2, "hamb");

		assertMatchesGroup(machine, "aalhammbhambra", 0, "aalhammbhambra");
		assertMatchesGroup(machine, "aalhammbhambra", 1, "aalhammbhambra");
		assertMatchesGroup(machine, "aalhammbhambra", 2, "hammbhamb");
		
		assertMatchesGroup(machine, "alra", 0, "alra");
		assertMatchesGroup(machine, "alra", 1, "alra");
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

		assertMatchesGroup(machine, "b", 0, "b");
		assertNoGroup(machine, "b", 1);

		assertMatchesGroup(machine, "db", 0, "db");
		assertNoGroup(machine, "db", 1);

		assertMatchesGroup(machine, "bcdb", 0, "bcdb");
		assertMatchesGroup(machine, "bcdb", 1, "bc");

		assertMatchesGroup(machine, "acdb", 0, "acdb");
		assertMatchesGroup(machine, "acdb", 1, "c");
		
		assertMatchesGroup(machine, "abdb", 0, "abdb");
		assertMatchesGroup(machine, "abdb", 1, "b");

		assertMatchesGroup(machine, "ab", 0, "ab");
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
	
	private static void assertMatches(StateMachine<String> machine, String target) {
		assertTrue(
				test(machine, target) >= 0,
				"Machine failed to accept an input it should have: " + target
		);
	}

	private static void assertConsistant(
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

	private static StateMachine<String> getMachine(String exp) {
		RegexParser regexParser = new RegexParser();
		Expression expression = regexParser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0", expression, PARSER, MATCHER);
	}
	
	private static void assertNotMatches(StateMachine<String> machine, String target) {
		assertFalse(
				test(machine, target) >= 0,
				"Machine accepted input it should not have: " + target
		);
	}

	private static void assertMatchesGroup(
			@NonNull StateMachine<String> machine,
			@NonNull String input,
			int group,
			@NonNull String expected
	) {
		Match<String> match = machine.match(input, 0);
		String matchedGroup = match.group(group);
		assertEquals(expected, matchedGroup);
	}

	private static void assertNoGroup(
			@NonNull StateMachine<String> machine,
			@NonNull String input,
			int group
	) {
		Match<String> match = machine.match(input, 0);
		assertNull(match.group(group));
	}
	
	private static int test(StateMachine<String> machine, String string) {
		Match<String> match = machine.match(string, 0);
		return match.end();
	}
}
