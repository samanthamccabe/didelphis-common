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
import org.didelphis.language.automata.interfaces.LanguageParser;
import org.didelphis.language.automata.interfaces.StateMachine;
import org.didelphis.language.automata.sequences.SequenceMatcher;
import org.didelphis.language.automata.sequences.SequenceParser;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;

/**
 * @author Samantha Fiona McCabe
 * @date 1/31/2016
 */
public class NegativeStateMachineTest {

	private static final FeatureMapping<Integer> MAPPING
			= new FeatureModelLoader<>(IntegerFeature.INSTANCE,
			ClassPathFileHandler.INSTANCE,
			Collections.emptyList()
	).getFeatureMapping();

	private static final SequenceFactory<Integer> FACTORY = loadModel();

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
		fail(machine, "a");
		fail(machine, "aa");

		test(machine, "b");
		test(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<Sequence<Integer>> machine = getMachine("!a?b#");
		fail(machine, "ab");
		fail(machine, "c");

		test(machine, "bb");
		test(machine, "b");
	}

	@Test
	void testBasic03() {
		StateMachine<Sequence<Integer>> machine = getMachine("!a*b#");
		fail(machine, "ab");
		fail(machine, "aab");
		fail(machine, "aaab");
		fail(machine, "c");

		//		fail(machine, "bab"); // TODO: actually it is unclear if this should pass or fail
		//		fail(machine, "bbab"); // TODO: actually it is unclear if this should pass or fail

		test(machine, "b");
		test(machine, "bb");
		test(machine, "bbb");
	}

	@Test
	void testGroup01() {
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)");
		fail(machine, "ab");

		test(machine, "aa");
		test(machine, "ac");
		test(machine, "aab");

		// These are too short
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	void testGroup02() {
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)");
		fail(machine, "ab");

		test(machine, "aa");
		test(machine, "ac");
		test(machine, "aab");

		// These are too short
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	void testGroup03() {
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)*xy#");
		fail(machine, "abxy");
		fail(machine, "ababxy");

		test(machine, "xy");
		test(machine, "xyxy");
		test(machine, "xyxyxy");

		// These are too short
		fail(machine, "aabxy");
		fail(machine, "babxy");
		fail(machine, "cabxy");
	}

	@Test
	void testGroup04() {
		// 2017-12-25: !(ab)+ == (!(ab))+ ?
		//             !(ab)+ != !((ab)+) - this is the correct interpretation
		StateMachine<Sequence<Integer>> machine = getMachine("!(ab)+xy#");

		test(machine, "aaxy");
		test(machine, "acxy");
		test(machine, "cbxy");
		test(machine, "ccxy");
		
		fail(machine, "abxy");
		fail(machine, "ababxy");
		fail(machine, "abababxy");

		fail(machine, "aaabxy");
		fail(machine, "acabxy");

		// These are too short
		fail(machine, "aabxy");
		fail(machine, "babxy");
		fail(machine, "cabxy");
	}

	@Test
	void testSet01() {
		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}");
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");

		test(machine, "x");
		test(machine, "y");
		test(machine, "z");
	}

	@Test
	void testSet02() {
		StateMachine<Sequence<Integer>> machine = getMachine("#!{a b c}#");
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");

		test(machine, "x");
		test(machine, "y");
		test(machine, "z");
	}

	@Test
	void testSet03() {
		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}+#");
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");

		// Length 2 - exhaustive
		fail(machine, "aa");
		fail(machine, "ba");
		fail(machine, "ca");

		fail(machine, "ab");
		fail(machine, "bb");
		fail(machine, "cb");

		fail(machine, "ac");
		fail(machine, "bc");
		fail(machine, "cc");

		// Length 3 - partial
		fail(machine, "acb");
		fail(machine, "bac");
		fail(machine, "cba");
		fail(machine, "acc");
		fail(machine, "baa");
		fail(machine, "cbb");
		fail(machine, "aca");
		fail(machine, "bab");
		fail(machine, "cbc");

		// Pass
		test(machine, "x");
		test(machine, "y");
		test(machine, "z");

		test(machine, "xxy");
		test(machine, "yyz");
		test(machine, "zzx");
	}

	@Test
	void testSet03Special() {

		Pattern pattern = Pattern.compile("[^abc]+$");

		StateMachine<Sequence<Integer>> machine = getMachine("!{a b c}+#");

		test(machine, "xxx");

		fail(machine, "aaa");
		fail(machine, "aax");
		fail(machine, "xaa");
		fail(machine, "xax");
		fail(machine, "axx");
		fail(machine, "xxa");
		fail(machine, "xa");
		fail(machine, "yb");
		fail(machine, "zc");

		fail(machine, "xya");
		fail(machine, "yzb");
		fail(machine, "zxc");
	}

	@Test
	void testVariables01() {

		String string = "C = p t k";

		String expression = "!C";

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
		);

		test(machine, "a");
		test(machine, "b");
		test(machine, "c");

		fail(machine, "p");
		fail(machine, "t");
		fail(machine, "k");
	}

	@Test
	void testVariables02() {

		String string = "C = ph th kh";

		String expression = "!C";

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
		);

		test(machine, "pp");
		test(machine, "tt");
		test(machine, "kk");

		// These are too short
		fail(machine, "p");
		fail(machine, "t");
		fail(machine, "k");

		fail(machine, "ph");
		fail(machine, "th");
		fail(machine, "kh");
	}

	@Test
	void testVariables03() {

		String string = "C = ph th kh kwh";
		String expression = "!C";

		StateMachine<Sequence<Integer>> machine = getMachine(
				parse(string),
				expression
		);

		test(machine, "pp");
		test(machine, "tt");
		test(machine, "kk");
		test(machine, "kw");
		test(machine, "kkw");

		// These are too short
		fail(machine, "p");
		fail(machine, "t");
		fail(machine, "k");

		fail(machine, "ph");
		fail(machine, "th");
		fail(machine, "kh");

		fail(machine, "kwh");
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
		SequenceFactory<Integer> factory = new SequenceFactory<>(MAPPING,
				new HashSet<>(),
				FormatterMode.NONE
		);

		SequenceParser<Integer> parser = new SequenceParser<>(factory);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		Expression expression = parser.parseExpression(exp);
		return StandardStateMachine.create("M0", expression, parser, matcher, FORWARD);
	}

	private static StateMachine<Sequence<Integer>> getMachine(
			Map<String, Collection<Sequence<Integer>>> map, String exp
	) {
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY,
				new GeneralMultiMap<>(map, Suppliers.ofHashSet())
		);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create("M0", parser.parseExpression(exp), parser, matcher, FORWARD);
	}

	private static <T> void test(
			StateMachine<T> stateMachine, String target
	) {
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {

			Collection<Integer> matchIndices = testMachine(stateMachine, target);
			Assertions.assertFalse(matchIndices.isEmpty(),
					"Machine failed to accept input: " + target
			);
		});
	}

	private static <T> void fail(
			StateMachine<T> stateMachine, 
			String target
	) {
		
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			Collection<Integer> matchIndices = testMachine(stateMachine, target);
			Assertions.assertTrue(
					matchIndices.isEmpty(),
					"Machine accepted input it should not have: " + target
			);			
		});
	}

	private static <T> Collection<Integer> testMachine(
			StateMachine<T> stateMachine, String target
	) {
		LanguageParser<T> parser = stateMachine.getParser();
		T sequence = parser.transform(target);
		return stateMachine.getMatchIndices(0, sequence);
	}
}
