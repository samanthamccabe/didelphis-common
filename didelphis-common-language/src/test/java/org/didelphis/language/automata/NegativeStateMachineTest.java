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
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.didelphis.language.parsing.ParseDirection.FORWARD;

/**
 * @author Samantha Fiona McCabe
 * @date 1/31/2016
 */
class NegativeStateMachineTest {

	private static final Logger LOG = Logger.create(NegativeStateMachineTest.class);
	
	private static final boolean  TIMEOUT  = false;
	private static final Duration DURATION = Duration.ofSeconds(1);
	
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

		fail(machine, "aaxyZ");
		fail(machine, "acxyZ");
		fail(machine, "cbxyZ");
		fail(machine, "ccxyZ");
		
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
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
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
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY, mMap);
		SequenceMatcher<Integer> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create(
				"M0",
				parser.parseExpression(exp, FORWARD),
				parser,
				matcher
		);
	}

	private static void test(
			StateMachine<Sequence<Integer>> stateMachine, String target
	) {
			Executable executable = () -> {
				Collection<Integer> indices = testMachine(stateMachine, target);
				Assertions.assertFalse(
						indices.isEmpty(),
						"Machine failed to accept input: " + target
				);
			};
		if (TIMEOUT && DURATION != null) {
			Assertions.assertTimeoutPreemptively(DURATION, executable);
		} else {
			try {
				executable.execute();
			} catch (Throwable throwable) {
				LOG.error("Unexpected failure encountered: {}", throwable);
			}
		}
	}

	private static void fail(
			StateMachine<Sequence<Integer>> stateMachine, 
			String target
	) {
		Executable executable = () -> {
			Collection<Integer> indices = testMachine(stateMachine, target);
			Assertions.assertTrue(
					indices.isEmpty(),
					"Machine accepted input it should not have: " + target
			);
		};

		if (TIMEOUT && DURATION != null) {
			Assertions.assertTimeoutPreemptively(DURATION, executable);
		} else {
			try {
				executable.execute();
			} catch (Throwable throwable) {
				LOG.error("Unexpected failure encountered: {}", throwable);
			}
		}
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Integer>> stateMachine, String target
	) {
		FeatureModel<Integer> model = FACTORY.getFeatureMapping().getFeatureModel();
		Sequence<Integer> sequence = new BasicSequence<>(model);
		SequenceParser<Integer> parser = new SequenceParser<>(FACTORY);
		if (target.startsWith("#")) sequence.add(parser.transform("#["));
		sequence.add(FACTORY.toSequence(target.replaceAll("#","")));
		if (target.endsWith("#")) sequence.add(parser.transform("]#"));

		Match<Sequence<Integer>> match = stateMachine.match(sequence, 0);
		return Collections.singleton(match.end());	}
}
