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

public interface Sequence
		extends ModelBearer, List<Segment>, Comparable<Sequence> {

	void add(@NonNull Sequence sequence);

	void insert(@NonNull Sequence sequence, int index);

	int indexOf(@NonNull Sequence target);

	int indexOf(@NonNull Sequence target, int start);

	@NonNull Sequence replaceAll(
			@NonNull Sequence source,
			@NonNull Sequence target
	);

	boolean contains(@NonNull Sequence sequence);

	boolean startsWith(@NonNull Segment segment);

	boolean startsWith(@NonNull Sequence sequence);

	@NonNull Sequence remove(int start, int end);

	boolean matches(@NonNull Sequence sequence);

	@NonNull Sequence subsequence(int from, int to);

	@NonNull Sequence subsequence(int from);

	@NonNull List<Integer> indicesOf(@NonNull Sequence sequence);

	@NonNull Sequence getReverseSequence();
}
