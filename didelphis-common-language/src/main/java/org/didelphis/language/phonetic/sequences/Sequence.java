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

package org.didelphis.language.phonetic.sequences;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.segments.Segment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by samantha on 1/30/17.
 */
public interface Sequence<T>
	  extends ModelBearer<T>,
	          List<Segment<T>>,
	          Comparable<Sequence<T>> {

	void add(@NotNull Sequence<T> sequence);

	void insert(@NotNull Sequence<T> sequence, int index);

	int indexOf(@NotNull Sequence<T> target);

	int indexOf(@NotNull Sequence<T> target, int start);

	@NotNull
	Sequence<T> replaceAll(@NotNull Sequence<T> source, @NotNull Sequence<T> target);

	boolean contains(@NotNull Sequence<T> sequence);

	boolean startsWith(@NotNull Segment<T> segment);

	boolean startsWith(@NotNull Sequence<T> sequence);

	@NotNull
	Sequence<T> remove(int start, int end);

	boolean matches(@NotNull Sequence<T> sequence);

	@NotNull
	Sequence<T> subsequence(int from, int to);

	@NotNull
	Sequence<T> subsequence(int from);

	@NotNull
	List<Integer> indicesOf(@NotNull Sequence<T> sequence);

	@NotNull
	BasicSequence<T> getReverseSequence();
}
