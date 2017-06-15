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

package org.didelphis.common.language.machines.sequences;

import org.didelphis.common.language.machines.interfaces.MachineMatcher;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.sequences.Sequence;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by samantha on 2/25/17.
 */
public class SequenceMatcher<T>
		implements MachineMatcher<Sequence<T>> {

	private final SequenceParser<T> parser;

	public SequenceMatcher(SequenceParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public int match(Sequence<T> target, Sequence<T> arc, int index) {

		Map<String, Collection<Sequence<T>>> specials = parser.getSpecials();

		SequenceFactory<T> factory = parser.getSequenceFactory();
		
		Sequence<T> tail = target.subsequence(index);

		if (Objects.equals(arc, factory.getBorderSequence())) {
			return (tail.isEmpty() || index == 0) ? index + 1 : -1;
		}

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
		
		if (arc.equals(factory.getDotSequence())) {
			if (!tail.isEmpty() && !tail.startsWith(factory.getBorderSegment())) {
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

	@Override
	public String toString() {
		return "SequenceMatcher{" + "parser=" + parser + '}';
	}
}
