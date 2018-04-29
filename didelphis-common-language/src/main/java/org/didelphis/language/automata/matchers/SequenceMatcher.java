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

package org.didelphis.language.automata.matchers;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.sequences.SequenceParser;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Tuple;

import java.util.Collection;
import java.util.List;

/**
 * Class {@code SequenceMatcher}
 */
@ToString
@EqualsAndHashCode
public class SequenceMatcher<T> implements LanguageMatcher<Sequence<T>> {

	private final SequenceParser<T> parser;
	private final MultiMap<Sequence<T>, Sequence<T>> specials;
	
	public SequenceMatcher(SequenceParser<T> parser) {
		this.parser = parser;
		specials = new GeneralMultiMap<>();
		
		for (Tuple<String, Collection<Sequence<T>>> tuple : parser.getSpecials()) {
			Sequence<T> key = parser.transform(tuple.getLeft());
			Collection<Sequence<T>> collection = tuple.getRight();
			specials.put(key, collection);
		}

	}

	@Override
	public int matches(
			@NonNull Sequence<T> input, 
			@NonNull Sequence<T> target,
			int index
	) {
		if (specials.containsKey(target)) {
			return specials.get(target)
					.stream()
					.filter(value -> input.subsequence(index).startsWith(value))
					.findFirst()
					.map(List::size)
					.orElse(-1);
		}
		
		return input.subsequence(index).startsWith(target) ? target.size() : -1;
	}
}
