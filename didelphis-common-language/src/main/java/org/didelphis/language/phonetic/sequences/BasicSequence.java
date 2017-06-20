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

import org.didelphis.language.phonetic.SpecificationBearer;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.language.phonetic.segments.Segment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Samantha Fiona McCabe
 */
public final class BasicSequence<T> extends AbstractSequence<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(BasicSequence.class);

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
	public void add(@NotNull Sequence<T> sequence) {
		validateModelOrFail(sequence);
		segmentList.addAll(sequence);
	}

	@Override
	public void insert(@NotNull Sequence<T> sequence, int index) {
		validateModelOrFail(sequence);
		segmentList.addAll(index, sequence);
	}

	@Override
	public int indexOf(@NotNull Sequence<T> target) {
		validateModelOrWarn(target);
		int size = target.size();
		if (size > size() || size == 0) {
			return -1;
		}

		int index = indexOf(target.get(0));
		if (index >= 0 && index + size <= size()) {
			// originally was equals, but use matches instead
			Sequence<T> subsequence = subsequence(index, index + size);
			if (!target.matches(subsequence)) {
				index = -1;
			}
		}
		return index;
	}

	@Override
	public int indexOf(@NotNull Sequence<T> target, int start) {
		validateModelOrWarn(target);
		int index = subsequence(start).indexOf(target);
		return (index >= 0) ? index + start : index;
	}

	@NotNull
	@Override
	public Sequence<T> replaceAll(@NotNull Sequence<T> source, @NotNull Sequence<T> target) {
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
	public boolean contains(@NotNull Sequence<T> sequence) {
		return indexOf(sequence) >= 0;
	}

	@Override
	public boolean startsWith(@NotNull Segment<T> segment) {
		validateModelOrWarn(segment);
		return !isEmpty() && segmentList.get(0).matches(segment);
	}

	@Override
	public boolean startsWith(@NotNull Sequence<T> sequence) {
		if (isEmpty() || sequence.size() > size()) {
			return false;
		}
		for (int i = 0; i < sequence.size(); i++) {
			if (!get(i).matches(sequence.get(i))) {
				return false;
			}
		}
		return true;
	}

	@NotNull
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
	 * be of the same length <p> Two sequences are consistent if each other if
	 * all corresponding segments are consistent; i.e. if, for every segment in
	 * each sequence, all corresponding features are equal OR one is undefined.
	 *
	 * @param sequence a segmentList to check against this one
	 * @return true if, for each segment in both sequences, all defined features
	 * 		in either segment are equal
	 */
	@Override
	public boolean matches(@NotNull Sequence<T> sequence) {
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

	@Override
	public Sequence<T> subsequence(int from, int to) {
		return new BasicSequence<>(subList(from, to), featureModel);
	}

	@Override
	public Sequence<T> subsequence(int from) {
		return new BasicSequence<>(subList(from, size()), featureModel);
	}

	@NotNull
	@Override
	public List<Integer> indicesOf(@NotNull Sequence<T> sequence) {
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
		StringBuilder sb = new StringBuilder(segmentList.size() * 2);
		for (Segment<T> segment : segmentList) {
			sb.append(segment.getSymbol());
		}
		return sb.toString();
	}

	@NotNull
	@Override
	public BasicSequence<T> getReverseSequence() {
		BasicSequence<T> reversed = new BasicSequence<>(this);
		Collections.reverse(reversed);
		return reversed;
	}

	@Override
	public boolean add(Segment<T> segment) {
		validateModelOrFail(segment);
		return segmentList.add(segment);
	}

	@Override
	public int compareTo(@NotNull Sequence<T> o) {
		for (int i = 0; i < size() && i < o.size(); i++) {
			int value = get(i).compareTo(o.get(i));
			if (value != 0) {
				return value;
			}
		}
		return size() > o.size() ? 1 : -1;
	}

	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel.getSpecification();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BasicSequence that = (BasicSequence) o;
		return Objects.equals(this.featureModel, that.featureModel) &&
				Objects.equals(this.segmentList, that.segmentList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureModel, segmentList);
	}

	private void validateModelOrWarn(SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			LOG.warn("Attempting to check a {} with an incompatible model!" +
							"" + "\n\t{}\t{}\n\t{}\t{}", that.getClass(), this, that,
					featureModel, that.getSpecification());
		}
	}

	private void validateModelOrFail(SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			throw new RuntimeException("Attempting to add " + that.getClass() +
					" with an incompatible model!\n" + '\t' + this + '\t' +
					featureModel + '\n' + '\t' + that + '\t' +
					that.getSpecification());
		}
	}
}
