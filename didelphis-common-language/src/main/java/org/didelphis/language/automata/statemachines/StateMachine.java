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

package org.didelphis.language.automata.statemachines;

import lombok.NonNull;
import org.didelphis.language.automata.Automaton;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.structures.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface {@code StateMachine}
 * 
 * @param <S> the type of data matched by the state machine
 * 
 * @author Samantha Fiona McCabe
 */
public interface StateMachine<S> extends Automaton<S> {

	@NonNull
	LanguageParser<S> getParser();

	String getId();

	/**
	 * Returns a map of ids to its associated graph. This
	 * ensures accessibility for automata which contain multiple embedded state
	 * automata.
	 * @return a {@link Map}, from id → {@link Graph}
	 */
	@NonNull
	Map<String, Graph<S>> getGraphs();

	@NonNull
	Map<String, StateMachine<S>> getStateMachines();

	@NonNull
	@Override
	default S replace(@NonNull S input, @NonNull S replacement) {
		LanguageParser<S> parser = getParser();
		
		 S sequence = parser.transform("");
		int size = parser.lengthOf(input);
		int cursor = 0;
		for (int i = 0; i < size;) {
			Match<S> match = match(input, i);
			if (match.matches()) {

				// Append non-matched
				S subSequence = parser.subSequence(input, cursor, i);
				sequence = parser.concatenate(sequence, subSequence);

				// Handle group references in replacement
				S newReplacement = parser.replaceGroups(replacement, match);
				sequence = parser.concatenate(sequence, newReplacement);
				
				i = match.end();
				cursor = i;
			} else {
				i++;
			}
		}

		S tail = parser.subSequence(input, cursor, size);
		return parser.concatenate(sequence, tail);
	}

	@NonNull
	@Override
	default List<S> split(@NonNull S input, int limit) {
		List<S> list = new ArrayList<>();

		LanguageParser<S> parser = getParser();
		int length = parser.lengthOf(input);
		int i = 0;
		int cursor = 0;

		while ((limit == -1 || list.size() < limit) && i < length) {
			Match<S> match = match(input, i);
			int end = match.end();
			if (end >= 0) {
				S sequence = parser.subSequence(input, cursor, i);
				list.add(sequence);
				cursor = end;
				i = cursor;
			} else {
				i++;
			}
		}
		if (cursor <= length) {
			list.add(parser.subSequence(input, cursor, length));
		}
		return list;
	}

	@NonNull
	@Override
	default Match<S> find(@NonNull S input) {
		int length = getParser().lengthOf(input);
		for (int i = 0; i < length; i++) {
			Match<S> match = match(input, i);
			if (match.matches()) {
				return match;
			}
		}
		return BasicMatch.empty(0);
	}

}
