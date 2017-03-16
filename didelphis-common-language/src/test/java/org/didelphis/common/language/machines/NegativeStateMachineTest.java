/******************************************************************************
 * Copyright (c) 2016. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.machines;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.enums.ParseDirection;
import org.didelphis.common.language.machines.interfaces.MachineParser;
import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.language.machines.sequences.SequenceMatcher;
import org.didelphis.common.language.machines.sequences.SequenceParser;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.VariableStore;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/31/2016
 */
public class NegativeStateMachineTest {

	private static final DoubleFeatureMapping MAPPING = DoubleFeatureMapping.getEmpty();

	private static final SequenceFactory<Double> FACTORY = loadModel();

	private static SequenceFactory<Double> loadModel() {
		String name = "AT_hybrid.model";
		FormatterMode mode = FormatterMode.INTELLIGENT;
		FileHandler handler = ClassPathFileHandler.getDefault();
		FeatureMapping<Double> mapping = DoubleFeatureMapping.load(name, handler, mode);
		return new SequenceFactory<>(mapping, mode);
	}
	
	@Test
	void testBasic01() {
		StateMachine<Sequence<Double>> machine = getMachine("!a");
		fail(machine, "a");
		fail(machine, "aa");

		test(machine, "b");
		test(machine, "c");
	}

	@Test
	void testBasic02() {
		StateMachine<Sequence<Double>> machine = getMachine("!a?b#");
		fail(machine, "ab");
		fail(machine, "c");

		test(machine, "bb");
		test(machine, "b");
	}

	@Test
	void testBasic03() {
		StateMachine<Sequence<Double>> machine = getMachine("!a*b#");
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
		StateMachine<Sequence<Double>> machine = getMachine("!(ab)");
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
		StateMachine<Sequence<Double>> machine = getMachine("!(ab)");
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
		StateMachine<Sequence<Double>> machine = getMachine("!(ab)*xy#");
		fail(machine, "abxy");
		fail(machine, "ababxy");

		test(machine, "xy");
		test(machine, "xyxy");
		test(machine, "xyxyxy");

		// These are too short
		fail(machine, "aab");
		fail(machine, "bab");
		fail(machine, "cab");
	}

	@Test
	void testeGroup04() {
		StateMachine<Sequence<Double>> machine = getMachine("!(ab)+xy#");
		fail(machine, "abxy");
		fail(machine, "ababxy");
		fail(machine, "abababxy");

		fail(machine, "aaabxy");
		fail(machine, "acabxy");

		test(machine, "aaxy");
		test(machine, "acxy");

		// These are too short
		fail(machine, "aab");
		fail(machine, "bab");
		fail(machine, "cab");
	}

	@Test
	void testSet01() {
		StateMachine<Sequence<Double>> machine = getMachine("!{a b c}");
		fail(machine, "a");
		fail(machine, "b");
		fail(machine, "c");

		test(machine, "x");
		test(machine, "y");
		test(machine, "z");
	}

	@Test
	void testSet02() {
		StateMachine<Sequence<Double>> machine = getMachine("#!{a b c}#");
		fail(machine, "#a");
		fail(machine, "#b");
		fail(machine, "#c");

		test(machine, "#x");
		test(machine, "#y");
		test(machine, "#z");
	}

	@Test
	void testSet03() {
		StateMachine<Sequence<Double>> machine = getMachine("!{a b c}+#");
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

		// Length 3 - tail
		// This is important as a distinction between:
		//     A) !{a b c}+
		//     B) !{a b c}+#
		// in that (A) will accept these but (B) will not:
		// TODO: these remain a problem ----------------------------------------
		fail(machine, "xa");
		fail(machine, "yb");
		fail(machine, "zc");

		// Pass
		test(machine, "x");
		test(machine, "y");
		test(machine, "z");

		test(machine, "xxy");
		test(machine, "yyz");
		test(machine, "zzx");
	}

	@Test
	void testVariables01() {

		VariableStore store = new VariableStore(FormatterMode.NONE);
		store.add("C = p t k");

		String expression = "!C";

		StateMachine<Sequence<Double>> machine = getMachine(store, expression);

		test(machine, "a");
		test(machine, "b");
		test(machine, "c");

		fail(machine, "p");
		fail(machine, "t");
		fail(machine, "k");
	}

	@Test
	void testVariables02() {

		VariableStore store = new VariableStore(FormatterMode.NONE);
		store.add("C = ph th kh");

		String expression = "!C";

		StateMachine<Sequence<Double>> machine = getMachine(store, expression);

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

		VariableStore store = new VariableStore(FormatterMode.NONE);
		store.add("C = ph th kh kwh");

		String expression = "!C";

		StateMachine<Sequence<Double>> machine = getMachine(store, expression);

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

	private static StateMachine<Sequence<Double>> getMachine(VariableStore store, String expression) {
		SequenceFactory<Double> factory = new SequenceFactory<>(
			  MAPPING,
			  store,
			  new HashSet<>(),
			  FormatterMode.NONE
		);

		SequenceParser<Double> parser = new SequenceParser<>(factory);
		SequenceMatcher<Double> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create("M0", expression, parser, matcher, ParseDirection.FORWARD);
	}
	
	private static StateMachine<Sequence<Double>> getMachine(String expression) {
		SequenceParser<Double> parser = new SequenceParser<>(FACTORY);
		SequenceMatcher<Double> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create("M0", expression, parser, matcher, ParseDirection.FORWARD);
	}

	private static void test(StateMachine<Sequence<Double>> stateMachine,
			String target) {
		Collection<Integer> matchIndices = testMachine(stateMachine, target);
		Assertions.assertFalse(matchIndices.isEmpty(),
				"Machine failed to accept input: " + target);
	}

	private static void fail(StateMachine<Sequence<Double>> stateMachine,
			String target) {
		Collection<Integer> matchIndices = testMachine(stateMachine, target);
		Assertions.assertTrue(matchIndices.isEmpty(),
				"Machine accepted input it should not have: " + target);
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Double>> stateMachine, String target) {
		MachineParser<Sequence<Double>> parser = stateMachine.getParser();
		Sequence<Double> sequence = parser.transform(target);
		return stateMachine.getMatchIndices(0, sequence);
	}
}
