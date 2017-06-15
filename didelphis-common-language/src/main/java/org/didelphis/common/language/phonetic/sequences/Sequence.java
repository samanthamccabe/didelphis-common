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

package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.ModelBearer;
import org.didelphis.common.language.phonetic.segments.Segment;

import java.util.Deque;
import java.util.List;

/**
 * Created by samantha on 1/30/17.
 */
public interface Sequence<T>
	  extends ModelBearer<T>,
	          Deque<Segment<T>>,
	          List<Segment<T>>,
	          Comparable<Sequence<T>> {

	void add(Sequence<T> sequence);

	void insert(Sequence<T> sequence, int index);

	int indexOf(Sequence<T> target);

	int indexOf(Sequence<T> target, int start);

	Sequence<T> replaceAll(Sequence<T> source, Sequence<T> target);

	boolean contains(Sequence<T> sequence);

	boolean startsWith(Segment<T> segment);

	boolean startsWith(Sequence<T> sequence);

	Sequence<T> remove(int start, int end);

	boolean matches(Sequence<T> sequence);

	Sequence<T> subsequence(int from, int to);

	Sequence<T> subsequence(int from);

	List<Integer> indicesOf(Sequence<T> sequence);
}
