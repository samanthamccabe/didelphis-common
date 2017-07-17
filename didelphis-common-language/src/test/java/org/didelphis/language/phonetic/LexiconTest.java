/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.language.phonetic;

import org.didelphis.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 3/29/17.
 */
class LexiconTest extends PhoneticTestBase {

	private static Lexicon<Integer> lexicon;
	private static Lexicon<Integer> empty;
	private static Lexicon<Integer> copy;

	@BeforeAll
	static void init() {
		List<List<String>> words = Arrays.asList(
				Arrays.asList("foo", "bar"),
				Arrays.asList("bof", "rob"),
				Arrays.asList("fob", "oof")
		);

		lexicon = Lexicon.fromRows(factory, words);
		empty = new Lexicon<>();
		copy = new Lexicon<>(lexicon);
	}

	@Test
	void add() {
		Lexicon<Integer> fromLists = new Lexicon<>();
		fromLists.add(factory.getSequence("foo"));
		fromLists.add(factory.getSequence("bof"));
		fromLists.add(factory.getSequence("fob"));

		List<String> list = Arrays.asList("foo", "bof", "fob");
		Lexicon<Integer> expected = Lexicon.fromSingleColumn(factory, list);
		assertEquals(fromLists, expected);
		assertNotEquals(fromLists, lexicon);
	}

	@Test
	void addList() {
		Lexicon<Integer> lists = new Lexicon<>(lexicon);
		lists.add(Arrays.asList(
				factory.getSequence("boo"),
				factory.getSequence("aff")
		));
		assertNotEquals(lexicon, lists);
	}

	@Test
	void testToString() {
		assertEquals(lexicon.toString(), copy.toString());
		assertNotEquals(lexicon.toString(), empty.toString());
	}

	@Test
	void equals() {
		assertEquals(lexicon, copy);
		assertNotEquals(lexicon, empty);
	}

	@Test
	void testHashCode() {
		assertEquals(lexicon.hashCode(), copy.hashCode());
		assertNotEquals(lexicon.hashCode(), empty.hashCode());
	}

	@Test
	void iterator() {
		Collection<List<Sequence<Integer>>> lex =new ArrayList<>();
		lexicon.iterator().forEachRemaining(lex::add);

		Collection<List<Sequence<Integer>>> cpy = new ArrayList<>();
		copy.iterator().forEachRemaining(cpy::add);

		Collection<List<Sequence<Integer>>> emt = new ArrayList<>();
		empty.iterator().forEachRemaining(emt::add);

		assertEquals(lex, cpy);
		assertNotEquals(lex, emt);
	}


}
