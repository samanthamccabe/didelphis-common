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

package org.didelphis.language.automata;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.*;
import static org.didelphis.language.parsing.ParseDirection.*;
import static org.junit.jupiter.api.Assertions.*;


class StandardStateMachineRegexTest extends StateMachineTestBase<String> {

	private static final RegexParser PARSER = new RegexParser();
	private static final String CONSIST_MESSAGE = "For input {} Java regex returned {} but Didelphis regex returned {}";

	@Override
	protected String transform(String input) {
		return PARSER.transform(input);
	}

	@Nested
	@DisplayName ("Conditions under which parsing is expected to fail")
	class TestParsingErrors {

		@Test
		void testStartingQuantifier() {
			assertThrowsParse("?a");
			assertThrowsParse("*a");
			assertThrowsParse("+a");
		}

		@Test
		void testSquareBracketInvalidRange02() {
			assertThrowsParse("[\\w-z]");
		}

		@Test
		void testSquareBracketInvalidRange01() {
			assertThrowsParse("[z-a]");
		}

		@Test
		void testSquareBracketInvalidRange03() {
			assertThrowsParse("[a-\\z]");
		}

		@Test
		void testBareEscape(){
			assertThrowsParse("\\");
			assertThrowsParse("a\\");
		}

		@Test
		void testShortHexEscape() {
			assertThrowsParse("\\0");
			assertThrowsParse("\\x0");
		}

		@Test
		void testShortUnicodeEscape() {
			assertThrowsParse("\\u");
			assertThrowsParse("\\u0");
			assertThrowsParse("\\u00");
			assertThrowsParse("\\u000");
		}

		private void assertThrowsParse(String string) {
			assertThrows(ParseException.class, () -> execute(string));
		}

		private void execute(String string) {
			Expression expression = PARSER.parseExpression(string);
			StandardStateMachine.create("M0", expression, PARSER);
		}
	}

	@Nested
	@DisplayName ("General pattern matching")
	class TestGeneralPatterns {

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
			assertMatches(machine, "atr");
			assertMatches(machine, "btr");
			assertMatches(machine, "ctr");
			assertMatches(machine, "dtr");
			assertMatches(machine, "xtr");
			assertMatches(machine, "ytr");
			assertMatches(machine, "etr");
			assertMatches(machine, "ftr");
			assertMatches(machine, ")tr");
			assertMatches(machine, "(tr");
			assertNotMatches(machine, "abtr");
			assertNotMatches(machine, "cdtr");
			assertNotMatches(machine, "xytr");
			assertNotMatches(machine, "eftr");
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
			StateMachine<String> nfa = getMachine("(a+l(ham+b)*ra)+");
			assertMatchesGroup(nfa, "alhambraalhambra", "alhambraalhambra", 0);
			assertMatchesGroup(nfa, "alhambra", "alhambra", 0);
			assertMatchesGroup(nfa, "alhambra", "alhambra", 1);
			assertMatchesGroup(nfa, "alhambra", "hamb", 2);
			assertMatchesGroup(nfa, "aalhammbhambra", "aalhammbhambra", 0);
			assertMatchesGroup(nfa, "aalhammbhambra", "aalhammbhambra", 1);
			assertMatchesGroup(nfa, "aalhammbhambra", "hammbhamb", 2);
			assertMatchesGroup(nfa, "alra", "alra", 0);
			assertMatchesGroup(nfa, "alra", "alra", 1);
			assertNoGroup(nfa, "alra", 2);
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
		void testComplex02() {
			String exp = "[rl]?[aeoāēō][iu]?[nmlr]?[ptkc]us";
			StateMachine<String> machine = getMachine(exp);
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
			StateMachine<String> machine = getMachine("[aeoāēō][ptkc]us");
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
		void testBinaryFeatureDefinition() {
			String regex = "([+−-])(\\w+)";
			StateMachine<String> machine = getMachine(regex);

			// it make sense that this should accept everything
			assertMatches(machine, "+breathy");
			assertMatches(machine, "-breathy");
			assertMatches(machine, "−breathy");
			assertMatches(machine, "+some_feature1");

			assertNotMatches(machine, "0f");
		}

		@Test
		void testNestedNegative01() {
			String regex = "^[^abc[^d]]$";
			StateMachine<String> machine = getMachine(regex);

			// it make sense that this should accept everything
			assertAll(() -> assertMatches(machine, "d"),
					() -> assertNotMatches(machine, "a"),
					() -> assertNotMatches(machine, "b"),
					() -> assertNotMatches(machine, "c"),
					() -> assertNotMatches(machine, "x")
			);
		}
	}

	@Nested
	@DisplayName ("Tests specific to ensuring consistency between Java and Didelphis regular expressions, where appropriate")
	class TestJavaPatternConsistency {

		@Test
		void testCase01() {
			String exp = "^(a)?a";
			StateMachine<String> machine = getMachine(exp);

			assertConsistant(exp, "a");
			assertConsistant(exp, "aa");

			assertMatch(machine, "a", 2, "a", null);
			assertMatch(machine, "aa", 2, "aa", "a");
		}

		@Test
		void testCase02() {
			StateMachine<String> machine = getMachine("^(aa(bb)?)+$");

			assertMatch(machine, "aabb", 3, "aabb", "aabb", "bb");
			assertMatch(machine, "aaaa", 3, "aaaa", "aa", null);
			assertMatch(machine, "aabbaa", 3, "aabbaa", "aabb", "bb");

			assertFalse(machine.matches(""));
			assertFalse(machine.matches("bb"));
		}

		@Test
		void testCase03() {
			@SuppressWarnings ("RegExpSingleCharAlternation")
			StateMachine<String> machine = getMachine("((a|b)?b)+");
			assertMatch(machine, "b", 3, "b", "b", null);
			assertMatch(machine, "bb", 3, "bb", "bb", "b");
			assertMatch(machine, "ab", 3, "ab", "ab", "a");
			assertMatch(machine, "abbx", 3, "abb", "ab", "a");

			assertFalse(machine.matches(""));
			assertFalse(machine.matches("a"));
		}

		@Test
		void testCase04() {
			StateMachine<String> machine = getMachine("(aaa)?aaa");
			assertMatches(machine, "aaa");

			assertMatch(machine, "aaa", 2, "aaa", null);
			assertMatch(machine, "aaaaaa", 2, "aaaaaa", "aaa");
			assertMatch(machine, "aaaa", 2, "aaa", null);

			assertFalse(machine.matches(""));
			assertFalse(machine.matches("a"));
			assertFalse(machine.matches("aa"));
		}

		@Test
		void testCase05() {
			StateMachine<String> machine = getMachine("^(a(b)?)+$");
			assertMatch(machine, "aba", 3, "aba", "ab", "b");
			assertMatch(machine, "abab", 3, "abab", "ab", "b");
			assertFalse(machine.matches("abb"));
		}

		@Test
		void testCase06() {
			StateMachine<String> machine = getMachine("^(a(b(c)?)?)?abc");
			assertMatch(machine, "abc", 4, "abc", null, null, null);
			assertMatch(machine, "abcabc", 4, "abcabc", "abc", "bc", "c");
		}

		@Test
		void testCase07() {
			StateMachine<String> machine = getMachine("^(a(b(c))).*");
			assertMatch(machine, "abcxx", 4, "abcxx", "abc", "bc", "c");
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
			@SuppressWarnings ("RegExpSingleCharAlternation")
			StateMachine<String> machine = getMachine("(a|b)+");
			assertMatches(machine, "ababab");
			assertNotMatches(machine, "cccccd");
		}

		@Test
		void testFeatureMatching01() {
			String exp = "([^\t]+)\t(.*)";
			StateMachine<String> machine = getMachine(exp);
			assertMatch(machine, "w\t+\t+", 3, "w\t+\t+", "w", "+\t+");
			assertConsistant(exp, "w\t+\t+");
		}

		@Test
		void testNestedNegative() {
			String exp = "[ab[^cd]]";
			assertConsistant(exp, "x");
			assertConsistant(exp, "a");
			assertConsistant(exp, "b");
			assertConsistant(exp, "c");
			assertConsistant(exp, "d");
			assertConsistant(exp, "x");
		}

		@Test
		void testComplex07() {
			String exp = "[rl]?[iu]?s";
			assertConsistant(exp, "s");
			assertConsistant(exp, "is");
			assertConsistant(exp, "us");
			assertConsistant(exp, "rs");
			assertConsistant(exp, "ls");
			assertConsistant(exp, "ris");
			assertConsistant(exp, "rus");
			assertConsistant(exp, "lis");
			assertConsistant(exp, "lus");
		}

		@Test
		void testWordCharacters() {
			String exp = "\\w+";
			assertConsistant(exp, "c");
			assertConsistant(exp, "cc");
			assertConsistant(exp, "cD");
			assertConsistant(exp, "cD0");
			assertConsistant(exp, "cD_0");
			assertConsistant(exp, "cD_09_a");
		}

		@Test
		void testConsistantNegative01() {
			String exp = "[^abc]";
			assertConsistant(exp, "a");
			assertConsistant(exp, "b");
			assertConsistant(exp, "c");
			assertConsistant(exp, "d");
		}

		@Test
		void testConsistantNegative02() {
			String exp = "^[^abc].[^abc]$";
			assertConsistant(exp, "aaa");
			assertConsistant(exp, "bac");
			assertConsistant(exp, "cab");
			assertConsistant(exp, "ddd");
			assertConsistant(exp, "d");
			assertConsistant(exp, "x");
			assertConsistant(exp, "dxdx");
			assertConsistant(exp, "dx");
		}

		@Test
		void testParentPathCapture() {
			String exp = "^(.*/)?[^/]+$";
			StateMachine<String> nfa = getMachine(exp);

			assertConsistant(exp, "/opt/x/z.sheet");
			assertConsistant(exp, "/root/file.txt");

			assertMatch(nfa, "/root/file.txt", 2, "/root/file.txt", "/root/");
			assertMatch(nfa, "/opt/x/z.sheet", 2, "/opt/x/z.sheet", "/opt/x/");
		}

		@Test
		void testGreedyCapture() {
			assertConsistant("(.+)(.+)", "abcd");
		}

		@Test
		void testNestedNegative02() {
			String exp = "^[^abc[d]]$";
			StateMachine<String> machine = getMachine(exp);

			assertMatches(machine, "x");

			assertNotMatches(machine, "a");
			assertNotMatches(machine, "b");
			assertNotMatches(machine, "c");
			assertNotMatches(machine, "d");

			assertConsistant(exp, "a");
			assertConsistant(exp, "b");
			assertConsistant(exp, "c");
			assertConsistant(exp, "d");
			assertConsistant(exp, "x");
		}

		@Test
		void testGroupPropagation01() {
			@SuppressWarnings ("RegExpRedundantEscape")
			String exp = "\\[([^\\]]+)\\]";
			StateMachine<String> machine = getMachine(exp);
			assertConsistant(exp, "[+breathy]");
			assertMatch(machine, "[+breathy]", 2, "[+breathy]", "+breathy");
		}

		@Test
		void testSquareBracketsNesting() {
			String exp = "\\[([^]]+)]$";

			assertConsistant(exp, "[a]");
			assertConsistant(exp, "[aa]");
			assertConsistant(exp, "[aaa]");

			assertConsistant(exp, "[]");
			assertConsistant(exp, "[]]");
		}

		@Test
		void testSquareBracketUnmatchedClose() {
			String exp = "\\[([^]]+)";
			assertConsistant(exp, "[]");
			assertConsistant(exp, "[a]");
			assertConsistant(exp, "[]]");
			assertConsistant(exp, "[a[");
			assertConsistant(exp, "[a[");
		}

		@Test
		void testHexEscape() {
			assertConsistant("\\x41", "A");
			assertConsistant("\\x4F", "O");
			assertConsistant("\\t\\x41", "\tA");
			assertConsistant("\\t\\x4F", "\tO");
		}

		@Test
		void testUnicodeEscape() {
			assertConsistant("\\u0041", "A");
			assertConsistant("\\u004F", "O");
			assertConsistant("\\t\\u0041", "\tA");
			assertConsistant("\\t\\u004F", "\tO");
		}

		@Test
		void testControlEscapes() {
			assertConsistant("\\t", "\t");
			assertConsistant("\\n", "\n");
			assertConsistant("\\r", "\r");
			assertConsistant("\\f", "\f");
			assertConsistant("\\a", "\u0007");
			assertConsistant("\\e", "\u001B");
		}

		@Test
		void testPredefinedClasses() {
			assertConsistant("\\d", "1", "2", "a");
			assertConsistant("\\D", "1", "2", "a");
			assertConsistant("\\s", " ", "\t", "\n", "X");
			assertConsistant("\\S", " ", "\t", "\n", "X");
			assertConsistant("\\w", "_", "a", " ", "!");
			assertConsistant("\\W", "_", "a", " ", "!");
		}

		@Test
		void testPosixClasses() {
			// A lower-case alphabetic character: [a-z]
			assertConsistant("\\p{Lower}",  "a");
			// An upper-case alphabetic character:[A-Z]
			assertConsistant("\\p{Upper}",  "A");
			// All ASCII:[\x00-\x7F]
			assertConsistant("\\p{ASCII}",  "a", "!", "\u0080");
			// An alphabetic character:[\p{Lower}\p{Upper}]
			assertConsistant("\\p{Alpha}",  "a", "Z","!");
			// A decimal digit: [0-9]
			assertConsistant("\\p{Digit}",  "0", "1", "A","!");
			// An alphanumeric character:[\p{Alpha}\p{Digit}]
			assertConsistant("\\p{Alnum}",  "0", "1", "A","!");
			// Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
			assertConsistant("\\p{Punct}",  "!", "@", "1", "A");
			// A visible character: [\p{Alnum}\p{Punct}]
			assertConsistant("\\p{Graph}",  "!", "@", "1", "A");
			// A printable character: [\p{Graph}\x20]
			assertConsistant("\\p{Print}",  "!", " ", "1", "A");
			// A space or a tab: [ \t]
			assertConsistant("\\p{Blank}",  " ", "\t", "_");
			// A control character: [\x00-\x1F\x7F]
			assertConsistant("\\p{Cntrl}",  "\u0000", "\u001F", "0");
			// A hexadecimal digit: [0-9a-fA-F]
			assertConsistant("\\p{XDigit}", "0", "A", "F", "Z");
			// A whitespace character: [ \t\n\x0B\f\r]
			assertConsistant("\\p{Space}",  "\t", "\n", "a");
		}

		/* For the sake of clarity, this method is used to test if our regex
		 * implementation has the same behavior as Java's Pattern class.
		 *
		 * The provided expression will be parsed by both systems, and then each
		 * input given will be checked for the same behavior: start and end
		 * indices, the number of groups, and their start and ends.
		 */
		private void assertConsistant(
				@Language ("regexp") String exp,
				String... inputs
		) {
			Pattern pattern = Pattern.compile(exp);
			StateMachine<String> machine = getMachine(exp);
			for (String input : inputs) {
				String target = transform(input);
				Match<String> match = machine.match(target, 0);
				int mEnd = match.end();

				Matcher matcher = pattern.matcher(input);
				int pEnd = matcher.find() ? matcher.end() : -1;

				String message = Templates.compile(CONSIST_MESSAGE, input, pEnd, mEnd);
				assertEquals(pEnd, mEnd, message);
				int groups = match.groupCount();

				assertEquals(matcher.groupCount() + 1, groups);

				for (int i = 1; i < groups && matcher.matches(); i++) {
					assertEquals(matcher.group(i), match.group(i),
							"Group " + i + " failed consistency check."
					);
				}
			}
		}
	}

	@Nested
	@DisplayName ("Non-matching operations (split, replace, etc.)")
	class TestSplitAndReplace {

		@Test
		void testSplit01() {
			StateMachine<String> machine = getMachine("a");
			assertEquals(asList("b", "b", "b"), machine.split("babab"));
			assertEquals(asList("", "b", "b", ""), machine.split("ababa"));
			assertEquals(asList("b", "b", ""), machine.split("baba"));
			assertEquals(asList("", "b", "b"), machine.split("abab"));
		}

		@Test
		void testSplit02() {
			StateMachine<String> machine = getMachine("[,;]\\s*|\\s+");
			List<String> expected = asList("a", "b", "c", "d");
			assertEquals(expected, machine.split("a, b;c d"));
		}

		@Test
		void testCaseImproperExpansion() {
			StateMachine<String> machine = getMachine("[+\\-−]");
			assertMatches(machine, "+");
			assertMatches(machine, "−");
			assertMatches(machine, "-");
			assertNotMatches(machine, "cccccd");
		}

		@Test
		void testReplace01() {
			StateMachine<String> machine = getMachine("a");
			assertEquals("b,b,b", machine.replace("babab", ","));
			assertEquals(",b,b,", machine.replace("ababa", ","));
			assertEquals("b,b,", machine.replace("baba", ","));
			assertEquals(",b,b", machine.replace("abab", ","));
		}

		@Test
		void testReplace02() {
			StateMachine<String> machine = getMachine("(a)(b)");
			assertEquals("bbaba", machine.replace("babab", "$2$1"));
			assertEquals("babaa", machine.replace("ababa", "$2$1"));
			assertEquals("bbaa", machine.replace("baba", "$2$1"));
			assertEquals("baba", machine.replace("abab", "$2$1"));
		}

		@Test
		void testReplace03() {
			StateMachine<String> machine = getMachine("(a)(b)");
			assertEquals("bbaxbax", machine.replace("babab", "$2$1x"));
			assertEquals("baxbaxa", machine.replace("ababa", "$2$1x"));
			assertEquals("bbaxa", machine.replace("baba", "$2$1x"));
			assertEquals("baxbax", machine.replace("abab", "$2$1x"));
		}

		@Test
		void testReplace04() {
			StateMachine<String> machine = getMachine("");
			assertEquals("b/a/b/a/b", machine.replace("babab", "/"));
			assertEquals("a/b/a/b/a", machine.replace("ababa", "/"));
			assertEquals("b/a/b/a", machine.replace("baba", "/"));
			assertEquals("a/b/a/b", machine.replace("abab", "/"));
		}
	}

	private static StateMachine<String> getMachine(@Language ("RegExp") String exp) {
		LanguageParser<String> regexParser = new RegexParser();
		Expression expression = regexParser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0", expression, PARSER);
	}
}
