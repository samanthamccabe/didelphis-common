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
