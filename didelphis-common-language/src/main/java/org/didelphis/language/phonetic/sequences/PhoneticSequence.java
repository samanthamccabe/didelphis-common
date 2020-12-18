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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class {@code PhoneticSequence}
 *
 * @param <T>
 */
@EqualsAndHashCode
public class PhoneticSequence implements Sequence {

	private static final Logger LOG = LogManager.getLogger(PhoneticSequence.class);

	private final List<Segment> segments;
	private final FeatureModel  featureModel;

	public PhoneticSequence(Sequence sequence) {
		segments = new ArrayList<>(sequence);
		featureModel = sequence.getFeatureModel();
	}

	public PhoneticSequence(Segment segment) {
		featureModel = segment.getFeatureModel();
		segments = new LinkedList<>();
		segments.add(segment);
	}

	public PhoneticSequence(FeatureModel model) {
		segments = new LinkedList<>();
		featureModel = model;
	}

	public PhoneticSequence(
			Collection<Segment> segments,
			FeatureModel model
	) {
		this.segments = new LinkedList<>(segments);
		featureModel = model;
	}

	@Override
	public void add(@NonNull Sequence sequence) {
		validateModelOrFail(sequence);
		segments.addAll(sequence);
	}

	@Override
	public void insert(@NonNull Sequence sequence, int index) {
		validateModelOrFail(sequence);
		segments.addAll(index, sequence);
	}

	@Override
	public int indexOf(@NonNull Sequence target) {
		return indexOf(target, 0);
	}

	@Override
	public int indexOf(@NonNull Sequence target, int start) {
		if (validateModelOrWarn(target)) {
			return -1;
		}
		int size = target.size();
		if (size > size() || size == 0) {
			return -1;
		}

		int index = start;
		while (index >= 0 && index + size <= size()) {
			Sequence subsequence = subsequence(index, index + size);
			if (target.matches(subsequence)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@NonNull
	@Override
	public Sequence replaceAll(
			@NonNull Sequence source,
			@NonNull Sequence target
	) {
		validateModelOrFail(source);
		validateModelOrFail(target);
		Sequence result = new PhoneticSequence(this);

		int index = result.indexOf(source);
		while (index >= 0) {
			if (index + source.size() <= result.size()) {
				result.remove(index, index + source.size());
				result.insert(target, index);
			}
			int from = index + target.size();
			Sequence subsequence = result.subsequence(from);
			index = subsequence.indexOf(source);
			if (index < 0) {
				break;
			}
			index += from;
		}
		return result;
	}

	@Override
	public boolean contains(@NonNull Sequence sequence) {
		if (validateModelOrWarn(sequence)) {
			return false;
		}
		return indexOf(sequence) >= 0;
	}

	@Override
	public boolean startsWith(@NonNull Segment segment) {
		if (validateModelOrWarn(segment)) {
			return false;
		}
		return !isEmpty() && segments.get(0).matches(segment);
	}

	@Override
	public boolean startsWith(@NonNull Sequence sequence) {
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
	public PhoneticSequence remove(int start, int end) {
		PhoneticSequence q = new PhoneticSequence(featureModel);
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
	 *
	 * @return true if, for each segment in both sequences, all defined features
	 * 		in either segment are equal
	 */
	@Override
	public boolean matches(@NonNull Sequence sequence) {
		validateModelOrFail(sequence);
		if (getSpecification().size() == 0) {
			return equals(sequence);
		}
		boolean matches = false;
		int size = size();
		if (size >= sequence.size()) {
			matches = true;
			for (int i = 0; i < size && matches; i++) {
				Segment x = get(i);
				Segment y = sequence.get(i);
				matches = x.matches(y);
			}
		}
		return matches;
	}

	@NonNull
	@Override
	public Sequence subsequence(int from, int to) {
		return new PhoneticSequence(subList(from, to), featureModel);
	}

	@NonNull
	@Override
	public Sequence subsequence(int from) {
		return new PhoneticSequence(subList(from, size()), featureModel);
	}

	@NonNull
	@Override
	public List<Integer> indicesOf(@NonNull Sequence sequence) {
		List<Integer> indices = new ArrayList<>();
		int index = indexOf(sequence);
		while (index >= 0) {
			indices.add(index);
			index = indexOf(sequence, index + sequence.size());
		}
		return indices;
	}

	@NonNull
	@Override
	public PhoneticSequence getReverseSequence() {
		Deque<Segment> linkedList = new LinkedList<>();
		for (Segment segment : segments) {
			linkedList.addFirst(segment);
		}
		return new PhoneticSequence(linkedList, featureModel);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Segment segment : segments) {
			String symbol = segment.getSymbol();
			sb.append(symbol);
		}
		return sb.toString();
	}

	@Override
	public int size() {
		return segments.size();
	}

	@Override
	public boolean isEmpty() {
		return segments.isEmpty();
	}

	@Override
	public boolean contains(Object object) {
		return segments.contains(object);
	}

	@NotNull
	@Override
	public Iterator<Segment> iterator() {
		return segments.iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return segments.toArray();
	}

	@NotNull
	@Override
	public <E> E[] toArray(@NotNull E[] array) {
		return segments.toArray(array);
	}

	@Override
	public boolean add(Segment segment) {
		validateModelOrFail(segment);
		return segments.add(segment);
	}

	@Override
	public boolean remove(Object object) {
		return segments.remove(object);
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> objects) {
		return segments.containsAll(objects);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends Segment> objects) {
		return segments.addAll(objects);
	}

	@Override
	public boolean addAll(
			int index, @NotNull Collection<? extends Segment> objects
	) {
		return segments.addAll(index, objects);
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> objects) {
		return segments.removeAll(objects);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> objects) {
		return segments.retainAll(objects);
	}

	@Override
	public void clear() {
		segments.clear();
	}

	@Override
	public Segment get(int index) {
		return segments.get(index);
	}

	@Override
	public Segment set(int index, Segment element) {
		return segments.set(index, element);
	}

	@Override
	public void add(int index, Segment element) {
		segments.add(index, element);
	}

	@Override
	public Segment remove(int index) {
		return segments.remove(index);
	}

	@Override
	public int indexOf(Object object) {
		return segments.indexOf(object);
	}

	@Override
	public int lastIndexOf(Object object) {
		return segments.lastIndexOf(object);
	}

	@NotNull
	@Override
	public ListIterator<Segment> listIterator() {
		return segments.listIterator();
	}

	@NotNull
	@Override
	public ListIterator<Segment> listIterator(int index) {
		return segments.listIterator(index);
	}

	@NotNull
	@Override
	public List<Segment> subList(int fromIndex, int toIndex) {
		return segments.subList(fromIndex, toIndex);
	}

	@Override
	public int compareTo(@NonNull Sequence o) {
		for (int i = 0; i < size() && i < o.size(); i++) {
			int value = get(i).compareTo(o.get(i));
			if (value != 0) {
				return value;
			}
		}
		return size() > o.size() ? 1 : -1;
	}

	@Override
	@NonNull
	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return featureModel.getSpecification();
	}

	private boolean validateModelOrWarn(@NonNull SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			LOG.warn("Attempting to check a {} with an incompatible model!\n\t{}\t{}\n\t{}\t{}",
					that.getClass(), this, that, featureModel, that.getSpecification());
			return true;
		}
		return false;
	}

	private void validateModelOrFail(@NonNull SpecificationBearer that) {
		if (!getSpecification().equals(that.getSpecification())) {
			throw new IllegalArgumentException(String.format("Attempting to add %s with an incompatible model!\n\t%s\t%s\n\t%s\t%s",
					that.getClass(), this, featureModel, that, that.getSpecification()));
		}
	}
}
