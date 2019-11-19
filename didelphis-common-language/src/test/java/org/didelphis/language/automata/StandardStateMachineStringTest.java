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

import lombok.NonNull;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.parsing.StringParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.didelphis.language.parsing.ParseDirection.*;
import static org.junit.jupiter.api.Assertions.*;

class StandardStateMachineStringTest extends StateMachineTestBase<String> {

	private static StringParser parser = new StringParser();

	@Override
	protected String transform(String input) {
		return parser.transform(input);
	}

	@Test
	void testStartingQuantifier() {
		assertThrowsParse("?a");
		assertThrowsParse("*a");
		assertThrowsParse("+a");
	}

	@Test
	void testSplit01() {
		StateMachine<String> machine = getMachine("");
		String sequence = "abc";
		List<String> split = machine.split(sequence);
		assertEquals(3, split.size());
	}

	@Test
	void testSplit02() {
		StateMachine<String> machine = getMachine("b");
		String sequence = "abc";
		List<String> split = machine.split(sequence);
		assertEquals(2, split.size());
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
	void testBoundaryOnly() {
		StateMachine<String> machine = getMachine("#");
		assertMatches(machine, "");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "abc");
	}

	@Test
	void testBoundaries1() {
		StateMachine<String> machine = getMachine("#a#");

		assertMatches(machine, "a");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBoundaries2() {
		StateMachine<String> machine = getMachine("#a");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");

		assertNotMatches(machine, "");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "x");
	}

	@Test
	void testBoundaries3() {
		StateMachine<String> machine = getMachine("a#");

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
		StateMachine<String> machine = getMachine("{ x ɣ }");

		assertMatches(machine, "x");
		assertMatches(machine, "ɣ");
		assertNotMatches(machine, " ");
		assertNotMatches(machine, "a");
	}

	@Test
	void testSets02() {
		String exp = "{ab {cd xy} ef}tr";
		StateMachine<String> machine = getMachine(exp);

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
		StateMachine<String> machine = getMachine("{cʰ  c  ɟ}");

		assertMatches(machine, "ɟ");
		assertMatches(machine, "c");
		assertMatches(machine, "cʰ");
		assertNotMatches(machine, "a");
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
		StateMachine<String> machine = getMachine("(ab)*#");

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
		StateMachine<String> machine = getMachine(exp);

		assertMatches(machine, "alhambra");
	}

	@Test
	void testComplexCapturingGroups01() {
		String exp = "(a+l(ham+b)*ra)+";
		StateMachine<String> machine = getMachine(exp);

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
		StateMachine<String> machine = getMachine("{r l}{i u}s");

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
		StateMachine<String> machine = getMachine("{r l}?{i u}?s");

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
		StateMachine<String> machine = getMachine("{r #}{i u}?s");

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
		StateMachine<String> machine = getMachine(exp);

		assertMatches(machine, "ācʰus");
		assertNotMatches(machine, "a");
	}

	@Test
	void testComplex03() {
		String expression = "a?{pʰ tʰ kʰ cʰ}us";
		StateMachine<String> machine = getMachine(expression);

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
		StateMachine<String> machine = getMachine(exp);

		assertMatches(machine, "apʰus");
		assertMatches(machine, "atʰus");
		assertMatches(machine, "akʰus");
		assertMatches(machine, "acʰus");
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
	void testComplex05() {

		String exp = "{ab* (cd?)+ ((ae)*f)+}tr";
		StateMachine<String> machine = getMachine(exp);

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
		StateMachine<String> machine = getMachine(exp);

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
		StateMachine<String> machine = getMachine("a#");
		assertMatches(machine, "a");
		assertNotMatches(machine, "ab");
	}

	@Test
	void testGroupsDotStar() {
		StateMachine<String> machine = getMachine("(a.)*cd#");

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
		StateMachine<String> machine = getMachine("m{a b #}");

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

		MultiMap<String, String> multiMap = new GeneralMultiMap<>();
		multiMap.add("CH", "ph");
		multiMap.add("CH", "th");
		multiMap.add("CH", "kh");

		StateMachine<String> machine = getMachine("aCHa", multiMap);

		assertMatches(machine, "apha");
		assertMatches(machine, "atha");
		assertMatches(machine, "akha");

		assertNotMatches(machine, "aCHa");
		assertNotMatches(machine, "apa");
		assertNotMatches(machine, "ata");
		assertNotMatches(machine, "aka");
	}

	@Test
	void testBasicNegative01() {
		StateMachine<String> machine = getMachine("!a");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "aa");

		assertMatches(machine, "b");
		assertMatches(machine, "c");
	}

	@Test
	void testBasicNegative02() {
		StateMachine<String> machine = getMachine("!a?b#");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "c");

		assertMatches(machine, "bb");
		assertMatches(machine, "b");
	}

	@Test
	void testBasicNegative03() {
		StateMachine<String> machine = getMachine("!a*b#");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "aab");
		assertNotMatches(machine, "aaab");
		assertNotMatches(machine, "c");

		assertNotMatches(machine, "bab");
		assertNotMatches(machine, "bbab");

		assertMatches(machine, "b");
		assertMatches(machine, "bb");
		assertMatches(machine, "bbb");
	}

	@Test
	void testGroup01() {
		StateMachine<String> machine = getMachine("!(ab)");
		assertNotMatches(machine, "ab");

		assertMatches(machine, "aa");
		assertMatches(machine, "ac");
		assertMatches(machine, "aab");

		// These are too short
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testGroup01_With_Captures() {
		StateMachine<String> machine = getMachine("!(ab)");

		assertMatchesGroup(machine, "aa", "aa", 0);
		assertMatchesGroup(machine, "aa", "aa", 1);

		assertMatchesGroup(machine, "ac", "ac", 0);
		assertMatchesGroup(machine, "ac", "ac", 1);

		assertMatchesGroup(machine, "aab", "aa", 0);
		assertMatchesGroup(machine, "aab", "aa", 1);
	}

	@Test
	void testGroup02_With_Captures() {
		StateMachine<String> machine = getMachine("!(ab(xy)?)");

		assertMatchesGroup(machine, "aa", "aa", 0);
		assertMatchesGroup(machine, "aa", "aa", 1);

		assertMatchesGroup(machine, "ac", "ac", 0);
		assertMatchesGroup(machine, "ac", "ac", 1);
		assertNoGroup(machine, "ac", 2);

		assertMatchesGroup(machine, "aab", "aa", 0);
		assertMatchesGroup(machine, "aab", "aa", 1);

		assertMatchesGroup(machine, "aazz", "aazz", 0);
		assertMatchesGroup(machine, "aazz", "aazz", 1);
		assertMatchesGroup(machine, "aaxz", "xz", 2);
	}

	@Test
	void testGroup03() {
		StateMachine<String> machine = getMachine("!(ab)*xy#");
		assertNotMatches(machine, "abxy");
		assertNotMatches(machine, "ababxy");

		assertMatches(machine, "xy");
		assertMatches(machine, "xyxy");
		assertMatches(machine, "xyxyxy");

		// These are too short
		assertNotMatches(machine, "aabxy");
		assertNotMatches(machine, "babxy");
		assertNotMatches(machine, "cabxy");
	}

	@Test
	void testGroup04() {
		// 2017-12-25: !(ab)+ == (!(ab))+ ?
		//             !(ab)+ != !((ab)+) - this is the correct interpretation
		StateMachine<String> machine = getMachine("!(ab)+xy#");

		assertMatches(machine, "aaxy");
		assertMatches(machine, "acxy");
		assertMatches(machine, "cbxy");
		assertMatches(machine, "ccxy");

		assertNotMatches(machine, "aaxyZ");
		assertNotMatches(machine, "acxyZ");
		assertNotMatches(machine, "cbxyZ");
		assertNotMatches(machine, "ccxyZ");

		assertNotMatches(machine, "abxy");
		assertNotMatches(machine, "ababxy");
		assertNotMatches(machine, "abababxy");

		assertNotMatches(machine, "aaabxy");
		assertNotMatches(machine, "acabxy");

		// These are too short
		assertNotMatches(machine, "aabxy");
		assertNotMatches(machine, "babxy");
		assertNotMatches(machine, "cabxy");
	}

	@Test
	void testSet01() {
		StateMachine<String> machine = getMachine("!{a b c}");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");

		assertMatches(machine, "x");
		assertMatches(machine, "y");
		assertMatches(machine, "z");
	}

	@Test
	void testSet02() {
		StateMachine<String> machine = getMachine("#!{a b c}#");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");

		assertMatches(machine, "x");
		assertMatches(machine, "y");
		assertMatches(machine, "z");
	}

	@Test
	void testSet03() {
		StateMachine<String> machine = getMachine("!{a b c}+#");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");

		// Length 2 - exhaustive
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "ba");
		assertNotMatches(machine, "ca");

		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "bb");
		assertNotMatches(machine, "cb");

		assertNotMatches(machine, "ac");
		assertNotMatches(machine, "bc");
		assertNotMatches(machine, "cc");

		// Length 3 - partial
		assertNotMatches(machine, "acb");
		assertNotMatches(machine, "bac");
		assertNotMatches(machine, "cba");
		assertNotMatches(machine, "acc");
		assertNotMatches(machine, "baa");
		assertNotMatches(machine, "cbb");
		assertNotMatches(machine, "aca");
		assertNotMatches(machine, "bab");
		assertNotMatches(machine, "cbc");

		// Pass
		assertMatches(machine, "x");
		assertMatches(machine, "y");
		assertMatches(machine, "z");

		assertMatches(machine, "xxy");
		assertMatches(machine, "yyz");
		assertMatches(machine, "zzx");
	}

	@Test
	void testSet03Special() {
		StateMachine<String> machine = getMachine("!{a b c}+#");

		assertMatches(machine, "xxx");

		assertNotMatches(machine, "aaa");
		assertNotMatches(machine, "aax");
		assertNotMatches(machine, "xaa");
		assertNotMatches(machine, "xax");
		assertNotMatches(machine, "axx");
		assertNotMatches(machine, "xxa");
		assertNotMatches(machine, "xa");
		assertNotMatches(machine, "yb");
		assertNotMatches(machine, "zc");

		assertNotMatches(machine, "xya");
		assertNotMatches(machine, "yzb");
		assertNotMatches(machine, "zxc");
	}

	@Test
	void testVariables01() {

		String string = "C = p t k";

		String expression = "!C";

		StateMachine<String> machine = getMachine(
				expression,
				parse(string)
		);

		assertMatches(machine, "a");
		assertMatches(machine, "b");
		assertMatches(machine, "c");

		assertNotMatches(machine, "p");
		assertNotMatches(machine, "t");
		assertNotMatches(machine, "k");
	}

	@Test
	void testVariables02() {

		String string = "C = ph th kh";

		String expression = "!C";

		StateMachine<String> machine = getMachine(
				expression,
				parse(string)
		);

		assertMatches(machine, "pp");
		assertMatches(machine, "tt");
		assertMatches(machine, "kk");

		// These are too short
		assertNotMatches(machine, "p");
		assertNotMatches(machine, "t");
		assertNotMatches(machine, "k");

		assertNotMatches(machine, "ph");
		assertNotMatches(machine, "th");
		assertNotMatches(machine, "kh");
	}

	@Test
	void testVariables03() {

		String string = "C = ph th kh kwh";
		String expression = "!C";

		StateMachine<String> machine = getMachine(expression, parse(string));

		assertMatches(machine, "pp");
		assertMatches(machine, "tt");
		assertMatches(machine, "kk");
		assertMatches(machine, "kw");
		assertMatches(machine, "kkw");

		// These are too short
		assertNotMatches(machine, "p");
		assertNotMatches(machine, "t");
		assertNotMatches(machine, "k");

		assertNotMatches(machine, "ph");
		assertNotMatches(machine, "th");
		assertNotMatches(machine, "kh");

		assertNotMatches(machine, "kwh");
	}

	@Test
	void testNesting01() {
		// It's unclear if this is necessarily the desired behavior but since
		// nested negations are clearly an edge case, it is reasonable to not
		// support them
		StateMachine<String> machine = getMachine("!(!(ab))");

		assertNotMatches(machine, "ab");

		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "ac");
		assertNotMatches(machine, "aab");

		// These are too short
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testGroupPropagation01() {
		StateMachine<String> machine = getMachine("[(!]+)]");
		assertMatch(machine, "[+breathy]", 2, "[+breathy]", "+breathy");
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

	@NonNull
	private static MultiMap<String, String> parse(String string) {
		String[] split = string.split("\\s*=\\s*");
		List<String> strings = Arrays.asList(split[1].split("\\s+"));
		Map<String, Collection<String>> map = new HashMap<>();
		map.put(split[0], strings);
		return new GeneralMultiMap<>(HashMap.class, HashSet.class, map);
	}

	private static StateMachine<String> getMachine(String expression) {
		return getMachine(expression, null);
	}

	private static void assertThrowsParse(String string) {
		assertThrows(
				ParseException.class,
				() -> {
					Expression expression = parser.parseExpression(string);
					StandardStateMachine.create("M0", expression, parser);
				}
		);
	}

	private static StateMachine<String> getMachine(
			String exp,
			MultiMap<String, String> specials
	) {
		parser = specials == null
				? new StringParser()
				: new StringParser(specials);
		Expression expression = parser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0",
				expression, parser);
	}
}
