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

package org.didelphis.language.automata.matching;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code BasicMatch}
 *
 * @date 2/22/18
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BasicMatch<S> implements Match<S> {
	
	int start;
	int end;
	
	S input;
	
	List<MatchGroup<S>> groups;

	public static <S> BasicMatch<S> empty(int size) {
		BasicMatch<S> match = new BasicMatch<>(null, -1, -1);
		for (int i = 0; i < size; i++) {
			match.addGroup(-1, -1, null);
		}
		return match;
	}
	
	public BasicMatch(S input, int start, int end) {
		this.start = start;
		this.end = end;
		this.input = input;
		
		groups = new ArrayList<>();
	}
	
	public void addGroup(int start, int end, S input) {
		groups.add(new MatchGroup<>(start, end , input));
	}
	
	@Override
	public int start() {
		return start;
	}

	@Override
	public int start(int group) {
		return groups.get(group).getStart();
	}

	@Override
	public int end() {
		return end;
	}

	@Override
	public int end(int group) {
		return groups.get(group).getEnd();
	}

	@Override
	public S group(int group) {
		return groups.get(group).getGroup();
	}

	@Override
	public int groupCount() {
		return groups.size();
	}
	
	@Value
	private static final class MatchGroup<T> {
		int start;
		int end;
		T group;
	}
}
