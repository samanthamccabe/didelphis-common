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

import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.structures.contracts.Delegating;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Created by samantha on 2/4/17.
 */
public abstract class AbstractSequence<T>
		implements Sequence<T>, Delegating<List<Segment<T>>> {

	protected final List<Segment<T>> segmentList;
	protected final FeatureModel<T> featureModel;

	protected AbstractSequence(@NotNull Sequence<T> sequence) {
		segmentList = new ArrayList<>(sequence);
		featureModel = sequence.getFeatureModel();
	}

	protected AbstractSequence(@NotNull Segment<T> segment) {
		this(segment.getFeatureModel());
		segmentList.add(segment);
	}

	protected AbstractSequence(FeatureModel<T> featureSpec) {
		segmentList = new LinkedList<>();
		featureModel = featureSpec;
	}

	protected AbstractSequence(@NotNull Collection<Segment<T>> segments,
			FeatureModel<T> featureSpec) {
		segmentList = new LinkedList<>(segments);
		featureModel = featureSpec;
	}

	@NotNull
	@Override
	public List<Segment<T>> getDelegate() {
		return segmentList;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Segment<T>> c) {
		return segmentList.addAll(index, c);
	}

	@Override
	public void replaceAll(UnaryOperator<Segment<T>> operator) {
		segmentList.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super Segment<T>> c) {
		segmentList.sort(c);
	}

	@Override
	public Segment<T> get(int index) {
		return segmentList.get(index);
	}

	@Override
	public Segment<T> set(int i, Segment<T> s) {
		return segmentList.set(i, s);
	}

	@Override
	public void add(int index, Segment<T> element) {
		segmentList.add(index, element);
	}

	@Override
	public Segment<T> remove(int index) {
		return segmentList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return segmentList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return segmentList.lastIndexOf(o);
	}

	@NotNull
	@Override
	public ListIterator<Segment<T>> listIterator() {
		return segmentList.listIterator();
	}

	@NotNull
	@Override
	public ListIterator<Segment<T>> listIterator(int index) {
		return segmentList.listIterator(index);
	}

	@NotNull
	@Override
	public List<Segment<T>> subList(int fromIndex, int toIndex) {
		return segmentList.subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<Segment<T>> spliterator() {
		return segmentList.spliterator();
	}

	@Override
	public boolean remove(Object o) {
		return segmentList.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public int size() {
		return segmentList.size();
	}

	@NotNull
	@Override
	public Iterator<Segment<T>> iterator() {
		return segmentList.iterator();
	}

	@Override
	public int hashCode() {
		int hash = 23;
		hash *= segmentList.hashCode();
		hash *= featureModel.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BasicSequence)) {
			return false;
		}

		BasicSequence<?> object = (BasicSequence<?>) obj;
		return featureModel.equals(object.featureModel) &&
				segmentList.equals(object.segmentList);
	}

	@Override
	public void forEach(Consumer<? super Segment<T>> action) {
		segmentList.forEach(action);
	}

	@Override
	public boolean isEmpty() {
		return segmentList.isEmpty();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return segmentList.toArray();
	}

	@NotNull
	@Override
	@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
	public <E> E[] toArray(@NotNull E[] a) {
		return segmentList.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return segmentList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Segment<T>> c) {
		return segmentList.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return segmentList.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super Segment<T>> filter) {
		return segmentList.removeIf(filter);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return segmentList.retainAll(c);
	}

	@Override
	public void clear() {
		segmentList.clear();
	}

	@Override
	public Stream<Segment<T>> stream() {
		return segmentList.stream();
	}

	@Override
	public Stream<Segment<T>> parallelStream() {
		return segmentList.parallelStream();
	}
}
