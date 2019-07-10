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

package org.didelphis.language.phonetic.sequences;

import lombok.NonNull;
import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.segments.Segment;

import java.util.List;

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
