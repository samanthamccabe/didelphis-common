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

package org.didelphis.language.automata.statemachines;

import lombok.NonNull;

import org.didelphis.language.automata.Automaton;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.LanguageParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface {@code StateMachine}
 *
 * @param <S> the type of data matched by the state machine
 */
public interface StateMachine<S> extends Automaton<S> {

	@NonNull
	LanguageParser<S> getParser();

	String getId();

	@NonNull
	@Override
	default S replace(@NonNull S input, @NonNull S replacement) {
		LanguageParser<S> parser = getParser();

		 S sequence = parser.transform("");
		int size = parser.lengthOf(input);
		int cursor = 0;
		int i = 0;
		while (i < size) {
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
