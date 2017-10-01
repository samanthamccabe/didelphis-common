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

package org.didelphis.language.machines.sequences;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.didelphis.language.machines.interfaces.MachineMatcher;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.sequences.Sequence;
import lombok.NonNull;

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
			@NonNull Sequence<T> target, @NonNull Sequence<T> arc, int index
	) {
		SequenceFactory<T> factory = parser.getSequenceFactory();

		Sequence<T> tail = target.subsequence(index);

		if (arc.equals(parser.getWordStart())) {
			return index == 0 ? index + 1 : -1;
		}
		
		if (arc.equals(parser.getWordEnd())) {
			return tail.isEmpty() ? index + 1 : -1;
		}

		if (Objects.equals(arc, parser.epsilon())) {
			return index;
		}

		Map<String, Collection<Sequence<T>>> specials = parser.getSpecials();

		String value = arc.toString();
		if (specials.containsKey(value)) {
			return specials.get(value)
					.stream()
					.filter(tail::startsWith)
					.findFirst()
					.map(special -> index + special.size())
					.orElse(-1);
		}

		if (arc.equals(factory.getDotSequence())) {
			if (!tail.isEmpty()
					&& !tail.startsWith(factory.getBorderSegment())) {
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
