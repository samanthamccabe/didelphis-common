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

/**
 * Class {@code SequenceMatcher}
 */
@ToString
@EqualsAndHashCode
public class SequenceMatcher<T> implements LanguageMatcher<Sequence<T>> {

	private final SequenceParser<T> parser;

	public SequenceMatcher(SequenceParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public boolean matches(
			@NonNull Sequence<T> input, 
			@NonNull Sequence<T> target,
			int index
	) {
			return input.subsequence(index).startsWith(target);
	}
}