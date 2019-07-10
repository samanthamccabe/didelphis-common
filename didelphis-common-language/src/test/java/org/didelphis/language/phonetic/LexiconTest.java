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
		fromLists.add(factory.toSequence("foo"));
		fromLists.add(factory.toSequence("bof"));
		fromLists.add(factory.toSequence("fob"));

		List<String> list = Arrays.asList("foo", "bof", "fob");
		Lexicon<Integer> expected = Lexicon.fromSingleColumn(factory, list);
		assertEquals(fromLists, expected);
		assertNotEquals(fromLists, lexicon);
	}

	@Test
	void addList() {
		Lexicon<Integer> lists = new Lexicon<>(lexicon);
		lists.add(Arrays.asList(
				factory.toSequence("boo"),
				factory.toSequence("aff")
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
