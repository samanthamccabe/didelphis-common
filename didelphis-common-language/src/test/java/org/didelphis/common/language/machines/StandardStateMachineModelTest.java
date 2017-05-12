/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
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
import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.language.machines.sequences.SequenceMatcher;
import org.didelphis.common.language.machines.sequences.SequenceParser;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/28/2015
 */
public class StandardStateMachineModelTest {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(StandardStateMachineModelTest.class);

	private static SequenceFactory<Double> factory;

	@BeforeAll
	public static void loadModel() {
		String name = "AT_hybrid.model";

		FormatterMode mode = FormatterMode.INTELLIGENT;

		FileHandler handler = ClassPathFileHandler.INSTANCE;
		
		factory = new SequenceFactory<>(DoubleFeatureMapping.load(name, handler, mode), mode);
	}
	
	@Test
	void testBasicStateMachine00() {
		assertThrows(ParseException.class, () -> getMachine("[]"));
	}

	@Test
	public void testDot() {
		StateMachine<Sequence<Double>> machine = getMachine(".");

		test(machine, "a");
		test(machine, "b");
		test(machine, "c");

		fail(machine, "");
	}

	@Test
	public void testBasicStateMachine01() {
		StateMachine<Sequence<Double>> machine = getMachine("[-con, +son, -hgh, +frn, -atr, +voice]");

		test(machine, "a");
		test(machine, "aa");

		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	public void testBasicStateMachine03() {
		StateMachine<Sequence<Double>> machine = getMachine("a[-con, +son, -hgh, +frn]+");

		fail(machine, "a");
		test(machine, "aa");
		test(machine, "aaa");
		test(machine, "aa̤");
		test(machine, "aa̤a");

		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	public void testBasicStateMachine02() {
		StateMachine<Sequence<Double>> machine = getMachine("aaa");

		test(machine, "aaa");

		fail(machine, "a");
		fail(machine, "aa");
		fail(machine, "b");
		fail(machine, "c");
	}

	@Test
	public void testStateMachineStar() {
		StateMachine<Sequence<Double>> machine = getMachine("aa*");

		test(machine, "a");
		test(machine, "aa");
		test(machine, "aaa");
		test(machine, "aaaa");
		test(machine, "aaaaa");
		test(machine, "aaaaaa");
	}

	@Test
	public void testComplex01() {
		StateMachine<Sequence<Double>> machine = getMachine("{a e o ā ē ō}{n m l r}?{pʰ tʰ kʰ cʰ}us");

		test(machine, "ācʰus");
		test(machine, "āncʰus");
		
		test(machine, "ātʰus");
		test(machine, "āntʰus");

		test(machine, "ārpʰus");
		test(machine, "ārpʰus");

		test(machine, "olkʰus");
		test(machine, "olkʰus");

		fail(machine, "entu");
		fail(machine, "āntus");
		fail(machine, "intʰus");
	}
	
	@Test
	public void testComplex02() {
		StateMachine<Sequence<Double>> machine = getMachine("{r l}?{a e o ā ē ō}{i u}?{n m l r}?{pʰ tʰ kʰ cʰ}us");

		test(machine, "ācʰus");
		test(machine, "rācʰus");
		test(machine, "lācʰus");

		test(machine, "aicʰus");
		test(machine, "raicʰus");
		test(machine, "laicʰus");

		test(machine, "āncʰus");
		test(machine, "rāncʰus");
		test(machine, "lāncʰus");

		test(machine, "ātʰus");
		test(machine, "rātʰus");
		test(machine, "lātʰus");

		test(machine, "aitʰus");
		test(machine, "raitʰus");
		test(machine, "laitʰus");

		test(machine, "āntʰus");
		test(machine, "rāntʰus");
		test(machine, "lāntʰus");

		fail(machine, "āntus");
		fail(machine, "rāntus");
		fail(machine, "lāntus");

		fail(machine, "intʰus");
		fail(machine, "rintʰus");
		fail(machine, "lintʰus");
	}

	@Test
	public void testComplex03() {
		StateMachine<Sequence<Double>> machine = getMachine("a?{pʰ tʰ kʰ cʰ}us");

		test(machine, "pʰus");
		test(machine, "tʰus");
		test(machine, "kʰus");
		test(machine, "cʰus");
		test(machine, "acʰus");
	}

	@Test
	public void testComplex04() {
		StateMachine<Sequence<Double>> machine = getMachine("{a e o ā ē ō}{pʰ tʰ kʰ cʰ}us");

		test(machine, "apʰus");
		test(machine, "atʰus");
		test(machine, "akʰus");
		test(machine, "acʰus");

		test(machine, "epʰus");
		test(machine, "etʰus");
		test(machine, "ekʰus");
		test(machine, "ecʰus");

		test(machine, "opʰus");
		test(machine, "otʰus");
		test(machine, "okʰus");
		test(machine, "ocʰus");

		test(machine, "āpʰus");
		test(machine, "ātʰus");
		test(machine, "ākʰus");
		test(machine, "ācʰus");

		test(machine, "ēpʰus");
		test(machine, "ētʰus");
		test(machine, "ēkʰus");
		test(machine, "ēcʰus");

		test(machine, "ōpʰus");
		test(machine, "ōtʰus");
		test(machine, "ōkʰus");
		test(machine, "ōcʰus");

		fail(machine, "ōpus");
		fail(machine, "ōtus");
		fail(machine, "ōkus");
		fail(machine, "ōcus");
	}

	@Test
	public void testComplex05() {
		StateMachine<Sequence<Double>> machine = getMachine("[-con, +voice, -creaky][-son, -voice, +vot]us");

		test(machine, "apʰus");
		test(machine, "atʰus");
		test(machine, "akʰus");
		test(machine, "acʰus");

		test(machine, "epʰus");
		test(machine, "etʰus");
		test(machine, "ekʰus");
		test(machine, "ecʰus");

		test(machine, "opʰus");
		test(machine, "otʰus");
		test(machine, "okʰus");
		test(machine, "ocʰus");

		test(machine, "āpʰus");
		test(machine, "ātʰus");
		test(machine, "ākʰus");
		test(machine, "ācʰus");

		test(machine, "ēpʰus");
		test(machine, "ētʰus");
		test(machine, "ēkʰus");
		test(machine, "ēcʰus");

		test(machine, "ōpʰus");
		test(machine, "ōtʰus");
		test(machine, "ōkʰus");
		test(machine, "ōcʰus");

		test(machine, "ipʰus");
		test(machine, "itʰus");
		test(machine, "ikʰus");
		test(machine, "icʰus");

		fail(machine, "ōpus");
		fail(machine, "ōtus");
		fail(machine, "ōkus");
		fail(machine, "ōcus");

		fail(machine, "a̰pʰus");
		fail(machine, "a̰tʰus");
		fail(machine, "a̰kʰus");
		fail(machine, "a̰cʰus");
	}


	private static StateMachine<Sequence<Double>> getMachine(String expression) {
		SequenceParser<Double> parser = new SequenceParser<>(factory);
		SequenceMatcher<Double> matcher = new SequenceMatcher<>(parser);
		return StandardStateMachine.create("M0", expression, parser, matcher, ParseDirection.FORWARD);
	}

	private static void test(StateMachine<Sequence<Double>> stateMachine,
			String target) {
		Collection<Integer> matchIndices = testMachine(stateMachine, target);
		String message = "Machine failed to accept input: " + target;
		assertFalse(matchIndices.isEmpty(),message);
	}

	private static void fail(StateMachine<Sequence<Double>> stateMachine,
			String target) {
		Collection<Integer> matchIndices = testMachine(stateMachine, target);
		String message = "Machine accepted input it should not have: " + target;
		assertTrue(matchIndices.isEmpty(), message);
	}

	private static Collection<Integer> testMachine(
			StateMachine<Sequence<Double>> stateMachine, String target) {
		Sequence<Double> sequence = factory.getSequence(target);
		return stateMachine.getMatchIndices(0, sequence);
	}
}