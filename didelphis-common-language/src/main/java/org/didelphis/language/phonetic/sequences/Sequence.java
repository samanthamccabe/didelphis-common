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

package org.didelphis.language.phonetic.sequences;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.segments.Segment;
import lombok.NonNull;

import java.util.List;

/**
 * Created by samantha on 1/30/17.
 */
public interface Sequence<T>
	  extends ModelBearer<T>,
	          List<Segment<T>>,
	          Comparable<Sequence<T>> {

	void add(@NonNull Sequence<T> sequence);

	void insert(@NonNull Sequence<T> sequence, int index);

	int indexOf(@NonNull Sequence<T> target);

	int indexOf(@NonNull Sequence<T> target, int start);

	@NonNull
	Sequence<T> replaceAll(@NonNull Sequence<T> source, @NonNull Sequence<T> target);

	boolean contains(@NonNull Sequence<T> sequence);

	boolean startsWith(@NonNull Segment<T> segment);

	boolean startsWith(@NonNull Sequence<T> sequence);

	@NonNull
	Sequence<T> remove(int start, int end);

	boolean matches(@NonNull Sequence<T> sequence);

	@NonNull
	Sequence<T> subsequence(int from, int to);

	@NonNull
	Sequence<T> subsequence(int from);

	@NonNull
	List<Integer> indicesOf(@NonNull Sequence<T> sequence);

	@NonNull
	Sequence<T> getReverseSequence();
}
