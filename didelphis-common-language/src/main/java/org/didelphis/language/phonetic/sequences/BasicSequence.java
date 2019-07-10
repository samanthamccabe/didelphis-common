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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.didelphis.language.phonetic.SpecificationBearer;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.utilities.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Class {@code BasicSequence}
 * @param <T>
 */
@EqualsAndHashCode(callSuper = true)
public class BasicSequence<T> extends AbstractSequence<T> {

	private static final Logger LOG = Logger.create(BasicSequence.class);

	public BasicSequence(Sequence<T> sequence) {
		super(sequence);
	}

	public BasicSequence(Segment<T> segment) {
		super(segment);
	}

	public BasicSequence(FeatureModel<T> featureSpecification) {
		super(featureSpecification);
	}

	public BasicSequence(Collection<Segment<T>> segments, FeatureModel<T> model) {
		super(segments, model);
	}

	@Override
	public void add(@NonNull Sequence<T> sequence) {
		validateModelOrFail(sequence);
		segments.addAll(sequence);
	}

	@Override
	public void insert(@NonNull Sequence<T> sequence, int index) {
		validateModelOrFail(sequence);
		segments.addAll(index, sequence);
	}

	@Override
	public int indexOf(@NonNull Sequence<T> target) {
		return indexOf(target, 0);
	}

	@Override
	public int indexOf(@NonNull Sequence<T> target, int start) {
		if (validateModelOrWarn(target)) {
			return -1;
		}
		int size = target.size();
		if (size > size() || size == 0) {
			return -1;
		}

		int index = start;
		while (index >= 0 && index + size <= size()) {
			Sequence<T> subsequence = subsequence(index, index + size);
			if (target.matches(subsequence)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@NonNull
	@Override
	public Sequence<T> replaceAll(@NonNull Sequence<T> source, @NonNull Sequence<T> target) {
		validateModelOrFail(source);
		validateModelOrFail(target);
		Sequence<T> result = new BasicSequence<>(this);

		int index = result.indexOf(source);
		while (index >= 0) {
			if (index + source.size() <= result.size()) {
				result.remove(index, index + source.size());
				result.insert(target, index);
			}
			int from = index + target.size();
			Sequence<T> subsequence = result.subsequence(from);
			index = subsequence.indexOf(source);
			if (index < 0) {
				break;
			}
			index += from;
		}
		return result;
	}

	@Override
	public boolean contains(@NonNull Sequence<T> sequence) {
		if (validateModelOrWarn(sequence)) {
			return false;
		}
		return indexOf(sequence) >= 0;
	}

	@Override
	public boolean startsWith(@NonNull Segment<T> segment) {
		if (validateModelOrWarn(segment)) {
			return false;
		}
		return !isEmpty() && segments.get(0).matches(segment);
	}

	@Override
	public boolean startsWith(@NonNull Sequence<T> sequence) {
		if (sequence.size() > size()) {
			return false;
		}
		int bound = sequence.size();
		for (int i = 0; i < bound; i++) {
			if (!get(i).matches(sequence.get(i))) {
				return false;
			}
		}
		return true;
	}

	@NonNull
	@Override
	public BasicSequence<T> remove(int start, int end) {
		BasicSequence<T> q = new BasicSequence<>(featureModel);
		for (int i = 0; i < end - start; i++) {
			q.add(remove(start));
		}
		return q;
	}

	/**
	 * Determines if a sequence is consistent with this sequence. Sequences must
	 * be of the same length. Two sequences are consistent if each other if
	 * all corresponding segments are consistent; i.e. if, for every segment in
	 * each sequence, all corresponding features are equal OR one is undefined.
	 *
	 * @param sequence a segments to check against this one
	 * @return true if, for each segment in both sequences, all defined features
	 * 		in either segment are equal
	 */
	@Override
	public boolean matches(@NonNull Sequence<T> sequence) {
		validateModelOrFail(sequence);
		if (getSpecification().size() == 0) {
			return equals(sequence);
		}
		boolean matches = false;
		int size = size();
		if (size >= sequence.size()) {
			matches = true;
			for (int i = 0; i < size && matches; i++) {
				Segment<T> x = get(i);
				Segment<T> y = sequence.get(i);
				matches = x.matches(y);
			}
		}
		return matches;
	}

	@NonNull
	@Override
	public Sequence<T> subsequence(int from, int to) {
		return new BasicSequence<>(subList(from, to), featureModel);
	}

	@NonNull
	@Override
	public Sequence<T> subsequence(int from) {
		return new BasicSequence<>(subList(from, size()), featureModel);
	}

	@NonNull
	@Override
	public List<Integer> indicesOf(@NonNull Sequence<T> sequence) {
		List<Integer> indices = new ArrayList<>();
		int index = indexOf(sequence);
		while (index >= 0) {
			indices.add(index);
			index = indexOf(sequence, index + sequence.size());
		}
		return indices;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Segment<T> segment : segments) {
			String symbol = segment.getSymbol();
			sb.append(symbol);
		}
		return sb.toString();
	}

	@NonNull
	@Override
	public BasicSequence<T> getReverseSequence() {
		Deque<Segment<T>> linkedList = new LinkedList<>();
		for (Segment<T> segment : getDelegate()) {
			linkedList.addFirst(segment);
		}
		return new BasicSequence<>(linkedList, getFeatureModel());
	}

	@Override
	public boolean add(Segment<T> segment) {
		validateModelOrFail(segment);
		return segments.add(segment);
	}

	@Override
	public int compareTo(@NonNull Sequence<T> o) {
		for (int i = 0; i < size() && i < o.size(); i++) {
			int value = get(i).compareTo(o.get(i));
			if (value != 0) {
				return value;
			}
		}
		return size() > o.size() ? 1 : -1;
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return featureModel.getSpecification();
	}

	private boolean validateModelOrWarn(@NonNull SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			LOG.warn("Attempting to check a {} with an incompatible model!" +
							"" + "\n\t{}\t{}\n\t{}\t{}", that.getClass(), this, that,
					featureModel, that.getSpecification());
			return true;
		}
		return false;
	}

	private void validateModelOrFail(@NonNull SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			throw new IllegalArgumentException("Attempting to add " + that.getClass() +
					" with an incompatible model!\n" + '\t' + this + '\t' +
					featureModel + '\n' + '\t' + that + '\t' +
					that.getSpecification());
		}
	}
}
