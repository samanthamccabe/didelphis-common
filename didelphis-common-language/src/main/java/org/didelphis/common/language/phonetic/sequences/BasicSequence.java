/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.Segment;
import org.didelphis.common.language.phonetic.SpecificationBearer;
import org.didelphis.common.language.phonetic.model.FeatureSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Samantha Fiona Morrigan McCabe
 */
public final class BasicSequence extends AbstractSequence {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(
		  BasicSequence.class);

	public static final Sequence EMPTY = new BasicSequence(FeatureSpecification.EMPTY);
	
	public BasicSequence(Sequence sequence) {
		super(sequence);
	}

	public BasicSequence(Segment segment) {
		super(segment);
	}

	// Used to produce empty copies with the same model
	public BasicSequence(FeatureSpecification featureSpecification) {
		super(featureSpecification);
	}

	private BasicSequence(Collection<Segment> segments,
	                      FeatureSpecification feaureSpec) {
		super(segments, feaureSpec);
	}

	@Override
	public boolean add(Segment segment) {
		validateModelOrFail(segment);
		return segmentList.add(segment);
	}

	@Override
	public void add(Sequence sequence) {
		validateModelOrFail(sequence);
		for (Segment segment : sequence) {
			segmentList.add(segment);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj instanceof BasicSequence) { return super.equals(obj); }
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode();
	}

	@Override
	public void insert(Sequence sequence, int index) {
		validateModelOrFail(sequence);

		segmentList.addAll(index, sequence);
	}

	@Override
	public BasicSequence remove(int start, int end) {
		BasicSequence q = new BasicSequence(specification);
		for (int i = 0; i < end - start; i++) {
			q.add(remove(start));
		}
		return q;
	}

	/**
	 * Determines if a sequence is consistent with this sequence.
	 * Sequences must be of the same length
	 * <p>
	 * Two sequences are consistent if each other if all corresponding segments
	 * are consistent; i.e. if, for every segment in each sequence, all
	 * corresponding features are equal OR one is undefined.
	 *
	 * @param sequence a segmentList to check against this one
	 *
	 * @return true if, for each segment in both sequences, all specified (non
	 * NaN) features in either segment are equal
	 */
	@Override
	public boolean matches(Sequence sequence) {
		validateModelOrFail(sequence);
		boolean matches = false;
		if (specification == FeatureSpecification.EMPTY) {
			matches = equals(sequence);
		} else {
			int size = size();
			if (size == sequence.size()) {
				matches = true;
				for (int i = 0; i < size && matches; i++) {
					Segment a = get(i);
					Segment b = sequence.get(i);
					matches = a.matches(b);
				}
			}
		}
		return matches;
	}

	@Override
	public Sequence subsequence(int from, int to) {
		return new BasicSequence(subList(from, to), specification);
	}

	@Override
	public Sequence subsequence(int from) {
		return new BasicSequence(subList(from, size()), specification);
	}

	@Override
	public int indexOf(Sequence target) {
		validateModelOrWarn(target);

		int size = target.size();
		if (size > size() || size == 0) { return -1; }

		int index = indexOf(target.getFirst());
		if (index >= 0 && index + size <= size()) {
			// originally was equals, but use matches instead
			Sequence subsequence = subsequence(index, index + size);
			if (!target.matches(subsequence)) {
				index = -1;
			}
		}
		return index;
	}

	@Override
	public int indexOf(Sequence target, int start) {
		validateModelOrWarn(target);
		int index = subsequence(start).indexOf(target);
		return (index >= 0) ? index + start : index;
	}

	@Override
	public Sequence replaceAll(Sequence source, Sequence target) {
		validateModelOrFail(source);
		validateModelOrFail(target);
		BasicSequence result = new BasicSequence(this);

		int index = result.indexOf(source);
		while (index >= 0) {
			if (index + source.size() <= result.size()) {
				result.remove(index, index + source.size());
				result.insert(target, index);
			}
			int from = index + target.size();
			Sequence subsequence = result.subsequence(from);
			index = subsequence.indexOf(source);
			if (index < 0) { break; }
			index += from;
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(segmentList.size() * 2);
//		sb.append('⟨');
		for (Segment segment : segmentList) {
			sb.append(segment.getSymbol());
		}
//		sb.append('⟩');
		return sb.toString();
	}

	@Deprecated
	public BasicSequence getReverseSequence() {
		BasicSequence reversed = new BasicSequence(specification);
		for (Segment g : segmentList) {
			reversed.addFirst(g);
		}
		return reversed;
	}

	@Override
	public void addFirst(Segment g) {
		validateModelOrFail(g);
		segmentList.add(0, g);
	}

	@Override
	public void addLast(Segment segment) {
		segmentList.addLast(segment);
	}

	@Override
	public boolean contains(Sequence sequence) {
		return indexOf(sequence) >= 0;
	}

	@Override
	public boolean startsWith(Segment segment) {
		validateModelOrWarn(segment);
		return !isEmpty() && segmentList.get(0).matches(segment);
	}

	@Override
	public boolean startsWith(Sequence sequence) {
		if (isEmpty() || sequence.size() > size()) { return false; }
		for (int i = 0; i < sequence.size(); i++) {
			if (!get(i).matches(sequence.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@Override
	public int compareTo(Sequence o) {

		for (int i = 0; i < size() && i < o.size(); i++) {
			int value = get(i).compareTo(o.get(i));
			if (value != 0) {
				return value;
			}
		}
		return size() > o.size() ? 1 : -1;
	}

	@Override
	public List<Integer> indicesOf(Sequence sequence) {
		List<Integer> indices = new ArrayList<>();

		int index = indexOf(sequence);

		while (index >= 0) {
			indices.add(index);
			index = indexOf(sequence, index + sequence.size());
		}
		return indices;
	}

	private void validateModelOrWarn(SpecificationBearer that) {
		if (!specification.equals(that.getSpecification())) {
			LOGGER.warn(
				  "Attempting to check a {} with an incompatible model!" + "\n\t{}\t{}\n\t{}\t{}",
				  that.getClass(), this, that, specification,
				  that.getSpecification());
		}
	}

	private void validateModelOrFail(SpecificationBearer that) {
		if (!specification.equals(that.getSpecification())) {
			throw new RuntimeException("Attempting to add " + that.getClass() + " with an incompatible model!\n" + '\t' + this + '\t' + specification + '\n' + '\t' + that + '\t' + that.getSpecification());
		}
	}
}
