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
