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
import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.SequenceMatcher;
import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;

/**
 * @author Samantha Fiona McCabe
 */
class NegativeStateMachineTest extends StateMachineTestBase<Sequence<Integer>> {
	
	private static final SequenceFactory<Integer> FACTORY = loadModel();
	
	private static SequenceParser<Integer> parser;
	private static SequenceMatcher<Integer> matcher;

	@Override
	protected Sequence<Integer> transform(String input) {
		return input.isEmpty()
				? FACTORY.toSequence(input)
				: parser.transform(input);
	}

	private static SequenceFactory<Integer> loadModel() {
		String name = "AT_hybrid.model";
		FormatterMode mode = FormatterMode.INTELLIGENT;
		return new SequenceFactory<>(new FeatureModelLoader<>(IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				name
		).getFeatureMapping(), mode);
	}

	@Test
	void testBasic01() {
		StateMachine<Sequence<Integer>> machine = getMachine("!a");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "aa");

		assertMatches(machine, "b");
		assertMatches(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<Sequence<Integer>> machine = getMachine("!a?b#");
		assertNotMatches(machine, "ab");
		assertNotMatches(machine, "c");

		assertMatches(machine, "bb");
		assertMatches(machine, "b");
	}
	
	@Test
	void testBasic03() {
		StateMachine<Sequence<Integer>> machine = getMachine("!a*b#");
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
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)");
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
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)");
		
		assertMatchesGroup(machine, "aa", "aa", 0);
		assertMatchesGroup(machine, "aa", "aa", 1);
		
		assertMatchesGroup(machine, "ac", "ac", 0);
		assertMatchesGroup(machine, "ac", "ac", 1);
		
		assertMatchesGroup(machine, "aab", "aa", 0);
		assertMatchesGroup(machine, "aab", "aa", 1);
	}
	
	@Test
	void testGroup02_With_Captures() {
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab(xy)?)");

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
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)*xy#");
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
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)+xy#");

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
		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");

		assertMatches(machine, "x");
		assertMatches(machine, "y");
		assertMatches(machine, "z");
	}

	@Test
	void testSet02() {
		StateMachine<Sequence<Integer>> machine = getMachine("#!{a b c}#");
		assertNotMatches(machine, "a");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");

		assertMatches(machine, "x");
		assertMatches(machine, "y");
		assertMatches(machine, "z");
	}

	@Test
	void testSet03() {
		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}+#");
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
		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}+#");

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

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
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

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
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

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
		);

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

	@NonNull
	private static Map<String, Collection<Sequence<Integer>>> parse(String string) {
		String[] split = string.split("\\s*=\\s*");
		List<String> strings = Arrays.asList(split[1].split("\\s+"));
		Map<String, Collection<Sequence<Integer>>> map = new HashMap<>();
		List<Sequence<Integer>> collect = strings.stream()
				.map(FACTORY::toSequence)
				.collect(Collectors.toList());
		map.put(split[0], collect);
		return map;
	}

	private static StateMachine<Sequence<Integer>> getMachine(String exp) {
		parser = new SequenceParser<>(FACTORY);
		matcher = new SequenceMatcher<>(parser);
		Expression expression = parser.parseExpression(exp, FORWARD);
		return StandardStateMachine.create("M0", expression, parser, matcher);
	}

	private static StateMachine<Sequence<Integer>> getMachine(
			Map<String, Collection<Sequence<Integer>>> map,
			String exp
	) {
		MultiMap<String, Sequence<Integer>> mMap = new GeneralMultiMap<>(
				map,
				Suppliers.ofHashSet()
		);
		parser = new SequenceParser<>(FACTORY, mMap);
		matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create(
				"M0",
				parser.parseExpression(exp, FORWARD),
				parser,
				matcher
		);
	}
}
