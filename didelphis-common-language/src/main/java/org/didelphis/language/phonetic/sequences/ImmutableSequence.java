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

import lombok.NonNull;
import org.didelphis.language.phonetic.segments.Segment;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Class {@code ImmutableSequence}
 *
 * @date 2017-06-21
 * @since 0.1.0
 */
public class ImmutableSequence<T> extends BasicSequence<T> {

	public ImmutableSequence(Sequence<T> sequence) {
		super(sequence);
	}

	public ImmutableSequence(Segment<T> segment) {
		super(segment);
	}

	@NonNull
	@Override
	public Segment<T> remove(int index) {
		throw unsupported();
	}

	@Override
	public void clear() {
		throw unsupported();
	}

	@Override
	public void add(int index, Segment<T> element) {
		throw unsupported();
	}

	@Override
	public boolean remove(Object o) {
		throw unsupported();
	}

	@NonNull
	@Override
	public Segment<T> set(int index, Segment<T> element) {
		throw unsupported();
	}

	@Override
	public boolean addAll(@NonNull Collection<? extends Segment<T>> c) {
		throw unsupported();
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		throw unsupported();
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		throw unsupported();
	}

	@Override
	public void replaceAll(UnaryOperator<Segment<T>> operator) {
		throw unsupported();
	}

	@Override
	public void sort(Comparator<? super Segment<T>> c) {
		throw unsupported();
	}

	@Override
	public boolean addAll(
			int index, @NonNull Collection<? extends Segment<T>> c
	) {
		throw unsupported();
	}

	@Override
	public boolean removeIf(Predicate<? super Segment<T>> filter) {
		throw unsupported();
	}

	@Override
	public void add(@NonNull Sequence<T> sequence) {
		throw unsupported();
	}

	@Override
	public void insert(@NonNull Sequence<T> sequence, int index) {
		throw unsupported();
	}

	@NonNull
	@Override
	public Sequence<T> replaceAll(
			@NonNull Sequence<T> source, @NonNull Sequence<T> target
	) {
		throw unsupported();
	}

	@NonNull
	@Override
	public BasicSequence<T> remove(int start, int end) {
		throw unsupported();
	}

	@NonNull
	@Override
	public String toString() {
		return super.toString() + " (immutable)";
	}

	@Override
	public boolean add(@NonNull Segment<T> segment) {
		throw unsupported();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ImmutableSequence)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() ^ toString().hashCode();
	}

	private static RuntimeException unsupported() {
		return new UnsupportedOperationException("Sequence is immutable.");
	}
}
