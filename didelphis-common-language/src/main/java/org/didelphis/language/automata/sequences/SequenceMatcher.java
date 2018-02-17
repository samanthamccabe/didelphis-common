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

package org.didelphis.language.automata.sequences;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.interfaces.MachineMatcher;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by samantha on 2/25/17.
 */
@ToString
@EqualsAndHashCode
public class SequenceMatcher<T> implements MachineMatcher<Sequence<T>> {

	private final SequenceParser<T> parser;

	public SequenceMatcher(SequenceParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public int match(
			@NonNull Sequence<T> target, 
			@NonNull Sequence<T> arc, 
			int index
	) {

		if (Objects.equals(arc, parser.epsilon())) {
			return index;
		}
		
		Sequence<T> sequence = new BasicSequence<>(target);

		sequence.add(0, parser.getWordStart().get(0));
		sequence.add(parser.getWordEnd());
		
		Sequence<T> tail = sequence.subsequence(index + 1);

		Map<String, Collection<Sequence<T>>> specials = parser.getSpecials();

		if (Objects.equals(arc, parser.epsilon())) {
			return index;
		}

		if (specials.containsKey(arc.toString())) {
			for (Sequence<T> special : specials.get(arc.toString())) {
				if (tail.startsWith(special)) {
					return index + special.size();
				}
			}
			return -1;
		}

		if (arc.equals(parser.getDot())) {
			if (!tail.isEmpty() 
					&& !tail.startsWith(parser.getWordStart()) 
					&& !tail.startsWith(parser.getWordEnd())) {
				return index + arc.size();
			}
		}

		if (tail.startsWith(arc)) {
			// Should work for both cases which have the same behavior
			return index + arc.size();
		}

		// Else: the pattern fails to match
		return -1;
	}
}
