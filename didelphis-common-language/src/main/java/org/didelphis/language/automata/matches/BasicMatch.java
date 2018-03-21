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

package org.didelphis.language.automata.matches;

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
 * @author Samantha Fiona McCabe
 * @date 2/22/18
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BasicMatch<T> implements Match<T> {
	
	int start;
	int end;
	
	T input;
	
	List<MatchGroup<T>> groups;

	public BasicMatch(T input,int start, int end) {
		this.start=start;
		this.end=end;
		this.input=input;
		
		groups = new ArrayList<>();
		groups.add(new MatchGroup<>(start, end , input));
	}
	
	public void addGroup(int start, int end, T input) {
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
	public T group(int group) {
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
