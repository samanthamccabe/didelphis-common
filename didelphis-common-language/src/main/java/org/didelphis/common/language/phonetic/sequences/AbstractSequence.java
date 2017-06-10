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

package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.didelphis.common.structures.contracts.Delegating;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
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
public abstract class AbstractSequence<N>
		implements Sequence<N>, Delegating<Deque<Segment<N>>> {
	
	protected AbstractSequence(Sequence<N> sequence) {
		segmentList = new LinkedList<>(sequence);
		featureModel = sequence.getFeatureModel();
	}

	protected AbstractSequence(Segment<N> segment) {
		this(segment.getFeatureModel());
		add(segment);
	}

	protected AbstractSequence(FeatureModel<N> featureSpec) {
		segmentList = new LinkedList<>();
		featureModel = featureSpec;
	}

	protected AbstractSequence(Collection<Segment<N>> segments, FeatureModel<N> featureSpec) {
		segmentList = new LinkedList<>(segments);
		featureModel = featureSpec;
	}
	
	protected final LinkedList<Segment<N>> segmentList;
	protected final FeatureModel<N> featureModel;

	@Override
	public Deque<Segment<N>> getDelegate() {
		return segmentList;
	}

	@Override
	public boolean remove(Object o) {
		return segmentList.remove(o);
	}

	@Override
	public boolean addAll(Collection<? extends Segment<N>> c) {
		return segmentList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Segment<N>> c) {
		return segmentList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return segmentList.removeAll(c);
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
	public Segment<N> get(int index) {
		return segmentList.get(index);
	}

	@Override
	public Segment<N> getFirst() {
		return get(0);
	}

	@Override
	public Segment<N> getLast() {
		return get(segmentList.size() - 1);
	}

	@Override
	public Segment<N> removeFirst() {
		return segmentList.removeFirst();
	}

	@Override
	public Segment<N> removeLast() {
		return segmentList.removeLast();
	}

	@Override
	public Segment<N> set(int i, Segment<N> s) {
		return segmentList.set(i, s);
	}

	@Override
	public void add(int index, Segment<N> element) {
		segmentList.add(index, element);
	}

	@Override
	public int size() {
		return segmentList.size();
	}

	@Override
	public Segment<N> remove(int index) {
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

	@Override
	public Segment<N> peek() {
		return segmentList.peek();
	}

	@Override
	public Segment<N> element() {
		return segmentList.element();
	}

	@Override
	public Segment<N> poll() {
		return segmentList.poll();
	}

	@Override
	public Segment<N> remove() {
		return segmentList.remove();
	}

	@Override
	public boolean offer(Segment<N> segment) {
		return segmentList.offer(segment);
	}

	@Override
	public boolean offerFirst(Segment<N> segment) {
		return segmentList.offerFirst(segment);
	}

	@Override
	public boolean offerLast(Segment<N> segment) {
		return segmentList.offerLast(segment);
	}

	@Override
	public Segment<N> peekFirst() {
		return segmentList.peekFirst();
	}

	@Override
	public Segment<N> peekLast() {
		return segmentList.peekLast();
	}

	@Override
	public Segment<N> pollFirst() {
		return segmentList.pollFirst();
	}

	@Override
	public Segment<N> pollLast() {
		return segmentList.pollLast();
	}

	@Override
	public void push(Segment<N> segment) {
		segmentList.push(segment);
	}

	@Override
	public Segment<N> pop() {
		return segmentList.pop();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return segmentList.removeFirstOccurrence(o);
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return segmentList.removeLastOccurrence(o);
	}

	@NotNull
	@Override
	public ListIterator<Segment<N>> listIterator() {
		return segmentList.listIterator();
	}

	@NotNull
	@Override
	public ListIterator<Segment<N>> listIterator(int index) {
		return segmentList.listIterator(index);
	}

	@NotNull
	@Override
	public Iterator<Segment<N>> descendingIterator() {
		return segmentList.descendingIterator();
	}
	
	@NotNull
	@Override
	public List<Segment<N>> subList(int fromIndex, int toIndex) {
		return segmentList.subList(fromIndex, toIndex);
	}

	@NotNull
	@Override
	public Iterator<Segment<N>> iterator() {
		return segmentList.iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return segmentList.toArray();
	}

	@NotNull
	@Override
	@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
	public <T> T[] toArray(@NotNull T[] a) {
		return segmentList.toArray(a);
	}

	@Override
	public Spliterator<Segment<N>> spliterator() {
		return segmentList.spliterator();
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
		if (this == obj) { return true; }
		if (!(obj instanceof BasicSequence)) { return false; }
		
		BasicSequence<?> object = (BasicSequence<?>) obj;
		return featureModel.equals(object.featureModel) 
		       && segmentList.equals(object.segmentList);
	}

	@Override
	public boolean removeIf(Predicate<? super Segment<N>> filter) {
		return segmentList.removeIf(filter);
	}

	@Override
	public Stream<Segment<N>> stream() {
		return segmentList.stream();
	}

	@Override
	public Stream<Segment<N>> parallelStream() {
		return segmentList.parallelStream();
	}

	@Override
	public void forEach(Consumer<? super Segment<N>> action) {
		segmentList.forEach(action);
	}

	@Override
	public void replaceAll(UnaryOperator<Segment<N>> operator) {
		segmentList.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super Segment<N>> c) {
		segmentList.sort(c);
	}

	@Override
	public boolean isEmpty() {
		return segmentList.isEmpty();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return segmentList.containsAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}
}
