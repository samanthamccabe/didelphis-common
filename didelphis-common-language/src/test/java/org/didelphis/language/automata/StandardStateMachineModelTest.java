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

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.parsing.SequenceParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 */
class StandardStateMachineModelTest extends StateMachineTestBase<Sequence<Integer>> {
	
	private static SequenceFactory<Integer> factory;
	private static SequenceParser<Integer> parser;

	@BeforeAll
	static void loadModel() {
		String name = "AT_hybrid.model";

		FormatterMode mode = FormatterMode.INTELLIGENT;

		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				name
		);
		FeatureMapping<Integer> featureMapping = loader.getFeatureMapping();
		factory = new SequenceFactory<>(featureMapping, mode);
	}
	
	@Test
	void testBasicStateMachine00() {
		assertThrows(ParseException.class, () -> getMachine("[]"));
	}

	@Test
	void testDot() {
		StateMachine<Sequence<Integer>> machine = getMachine(".");

		assertMatches(machine, "a");
		assertMatches(machine, "b");
		assertMatches(machine, "c");

		assertMatches(machine, "g");
		assertMatches(machine, "h");
		assertMatches(machine, "y");
	}

	@Test
	void testDotEmptyString() {
		StateMachine<Sequence<Integer>> machine = getMachine(".");
		assertNotMatches(machine, "");
	}
	
	@Test
	void testBasicStateMachine01() {
		String exp = "[-con, +son, -hgh, +frn, -atr, +voice]";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "a");
		assertMatches(machine, "aa");

		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasicStateMachine03() {
		String exp = "a[-con, +son, -hgh, +frn]+";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertNotMatches(machine, "a");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		assertMatches(machine, "aa̤");
		assertMatches(machine, "aa̤a");

		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testBasicStateMachine02() {
		StateMachine<Sequence<Integer>> machine = getMachine("aaa");

		assertMatches(machine, "aaa");

		assertNotMatches(machine, "a");
		assertNotMatches(machine, "aa");
		assertNotMatches(machine, "b");
		assertNotMatches(machine, "c");
	}

	@Test
	void testStateMachineStar() {
		StateMachine<Sequence<Integer>> machine = getMachine("aa*");

		assertMatches(machine, "a");
		assertMatches(machine, "aa");
		assertMatches(machine, "aaa");
		assertMatches(machine, "aaaa");
		assertMatches(machine, "aaaaa");
		assertMatches(machine, "aaaaaa");
	}

	@Test
	void testComplex01() {
		String exp = "{a e o ā ē ō}{n m l r}?{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "ācʰus");
		assertMatches(machine, "āncʰus");
		
		assertMatches(machine, "ātʰus");
		assertMatches(machine, "āntʰus");

		assertMatches(machine, "ārpʰus");
		assertMatches(machine, "ārpʰus");

		assertMatches(machine, "olkʰus");
		assertMatches(machine, "olkʰus");

		assertNotMatches(machine, "entu");
		assertNotMatches(machine, "āntus");
		assertNotMatches(machine, "intʰus");
	}
	
	@Test
	void testComplex02() {
		String exp = "{r l}?{a e o ā ē ō}{i u}?{n m l r}?{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "ācʰus");
		assertMatches(machine, "rācʰus");
		assertMatches(machine, "lācʰus");

		assertMatches(machine, "aicʰus");
		assertMatches(machine, "raicʰus");
		assertMatches(machine, "laicʰus");

		assertMatches(machine, "āncʰus");
		assertMatches(machine, "rāncʰus");
		assertMatches(machine, "lāncʰus");

		assertMatches(machine, "ātʰus");
		assertMatches(machine, "rātʰus");
		assertMatches(machine, "lātʰus");

		assertMatches(machine, "aitʰus");
		assertMatches(machine, "raitʰus");
		assertMatches(machine, "laitʰus");

		assertMatches(machine, "āntʰus");
		assertMatches(machine, "rāntʰus");
		assertMatches(machine, "lāntʰus");

		assertNotMatches(machine, "āntus");
		assertNotMatches(machine, "rāntus");
		assertNotMatches(machine, "lāntus");

		assertNotMatches(machine, "intʰus");
		assertNotMatches(machine, "rintʰus");
		assertNotMatches(machine, "lintʰus");
	}

	@Test
	void testComplex03() {
		String exp = "a?{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "pʰus");
		assertMatches(machine, "tʰus");
		assertMatches(machine, "kʰus");
		assertMatches(machine, "cʰus");
		assertMatches(machine, "acʰus");
	}

	@Test
	void testComplex04() {
		String exp = "{a e o ā ē ō}{pʰ tʰ kʰ cʰ}us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "apʰus");
		assertMatches(machine, "atʰus");
		assertMatches(machine, "akʰus");
		assertMatches(machine, "acʰus");

		assertMatches(machine, "epʰus");
		assertMatches(machine, "etʰus");
		assertMatches(machine, "ekʰus");
		assertMatches(machine, "ecʰus");

		assertMatches(machine, "opʰus");
		assertMatches(machine, "otʰus");
		assertMatches(machine, "okʰus");
		assertMatches(machine, "ocʰus");

		assertMatches(machine, "āpʰus");
		assertMatches(machine, "ātʰus");
		assertMatches(machine, "ākʰus");
		assertMatches(machine, "ācʰus");

		assertMatches(machine, "ēpʰus");
		assertMatches(machine, "ētʰus");
		assertMatches(machine, "ēkʰus");
		assertMatches(machine, "ēcʰus");

		assertMatches(machine, "ōpʰus");
		assertMatches(machine, "ōtʰus");
		assertMatches(machine, "ōkʰus");
		assertMatches(machine, "ōcʰus");

		assertNotMatches(machine, "ōpus");
		assertNotMatches(machine, "ōtus");
		assertNotMatches(machine, "ōkus");
		assertNotMatches(machine, "ōcus");
	}

	@Test
	void testComplex05() {
		String exp = "[-con, +voice, -creaky][-son, -voice, +vot]us";
		StateMachine<Sequence<Integer>> machine = getMachine(exp);

		assertMatches(machine, "apʰus");
		assertMatches(machine, "atʰus");
		assertMatches(machine, "akʰus");
		assertMatches(machine, "acʰus");

		assertMatches(machine, "epʰus");
		assertMatches(machine, "etʰus");
		assertMatches(machine, "ekʰus");
		assertMatches(machine, "ecʰus");

		assertMatches(machine, "opʰus");
		assertMatches(machine, "otʰus");
		assertMatches(machine, "okʰus");
		assertMatches(machine, "ocʰus");

		assertMatches(machine, "āpʰus");
		assertMatches(machine, "ātʰus");
		assertMatches(machine, "ākʰus");
		assertMatches(machine, "ācʰus");

		assertMatches(machine, "ēpʰus");
		assertMatches(machine, "ētʰus");
		assertMatches(machine, "ēkʰus");
		assertMatches(machine, "ēcʰus");

		assertMatches(machine, "ōpʰus");
		assertMatches(machine, "ōtʰus");
		assertMatches(machine, "ōkʰus");
		assertMatches(machine, "ōcʰus");

		assertMatches(machine, "ipʰus");
		assertMatches(machine, "itʰus");
		assertMatches(machine, "ikʰus");
		assertMatches(machine, "icʰus");

		assertNotMatches(machine, "ōpus");
		assertNotMatches(machine, "ōtus");
		assertNotMatches(machine, "ōkus");
		assertNotMatches(machine, "ōcus");

		assertNotMatches(machine, "a̰pʰus");
		assertNotMatches(machine, "a̰tʰus");
		assertNotMatches(machine, "a̰kʰus");
		assertNotMatches(machine, "a̰cʰus");
	}

	private static StateMachine<Sequence<Integer>> getMachine(String exp) {
		parser = new SequenceParser<>(factory);
		Expression expression = parser.parseExpression(exp);
		return StandardStateMachine.create("M0", expression, parser);
	}
	
	@Override
	protected Sequence<Integer> transform(String input) {
		return input.isEmpty()
				? factory.toSequence(input)
				: parser.transform(input);
	}
}
