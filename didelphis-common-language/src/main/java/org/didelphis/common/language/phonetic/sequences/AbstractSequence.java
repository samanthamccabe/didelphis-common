package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.Segment;
import org.didelphis.common.language.phonetic.model.FeatureSpecification;

import java.util.Arrays;
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
public abstract class AbstractSequence implements Sequence {
	
	protected AbstractSequence(Sequence sequence) {
		segmentList = new LinkedList<>(sequence);
		specification = sequence.getSpecification();
	}

	protected AbstractSequence(Segment segment) {
		this(segment.getSpecification());
		add(segment);
	}

	protected AbstractSequence(FeatureSpecification featureSpec) {
		segmentList = new LinkedList<>();
		specification = featureSpec;
	}

	protected AbstractSequence(Collection<Segment> segments, FeatureSpecification featureSpec) {
		segmentList = new LinkedList<>(segments);
		specification = featureSpec;
	}
	
	protected final LinkedList<Segment> segmentList;
	protected final FeatureSpecification specification;

	@Override
	public boolean remove(Object o) {
		return segmentList.remove(o);
	}

	@Override
	public boolean addAll(Collection<? extends Segment> c) {
		return segmentList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Segment> c) {
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
	public Segment get(int index) {
		return segmentList.get(index);
	}

	@Override
	public Segment getFirst() {
		return get(0);
	}

	@Override
	public Segment getLast() {
		return get(segmentList.size() - 1);
	}

	@Override
	public Segment removeFirst() {
		return segmentList.removeFirst();
	}

	@Override
	public Segment removeLast() {
		return segmentList.removeLast();
	}

	@Override
	public Segment set(int i, Segment s) {
		return segmentList.set(i, s);
	}

	@Override
	public void add(int index, Segment element) {
		segmentList.add(index, element);
	}

	@Override
	public int size() {
		return segmentList.size();
	}

	@Override
	public Segment remove(int index) {
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
	public Segment peek() {
		return segmentList.peek();
	}

	@Override
	public Segment element() {
		return segmentList.element();
	}

	@Override
	public Segment poll() {
		return segmentList.poll();
	}

	@Override
	public Segment remove() {
		return segmentList.remove();
	}

	@Override
	public boolean offer(Segment segment) {
		return segmentList.offer(segment);
	}

	@Override
	public boolean offerFirst(Segment segment) {
		return segmentList.offerFirst(segment);
	}

	@Override
	public boolean offerLast(Segment segment) {
		return segmentList.offerLast(segment);
	}

	@Override
	public Segment peekFirst() {
		return segmentList.peekFirst();
	}

	@Override
	public Segment peekLast() {
		return segmentList.peekLast();
	}

	@Override
	public Segment pollFirst() {
		return segmentList.pollFirst();
	}

	@Override
	public Segment pollLast() {
		return segmentList.pollLast();
	}

	@Override
	public void push(Segment segment) {
		segmentList.push(segment);
	}

	@Override
	public Segment pop() {
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

	@Override
	public ListIterator<Segment> listIterator() {
		return segmentList.listIterator();
	}

	@Override
	public ListIterator<Segment> listIterator(int index) {
		return segmentList.listIterator(index);
	}

	@Override
	public Iterator<Segment> descendingIterator() {
		return segmentList.descendingIterator();
	}

	@Override
	public Object clone() {
		return segmentList.clone();
	}

	@Override
	public List<Segment> subList(int fromIndex, int toIndex) {
		return segmentList.subList(fromIndex, toIndex);
	}

	@Override
	public Iterator<Segment> iterator() {
		return segmentList.iterator();
	}

	@Override
	public Object[] toArray() {
		int size = segmentList.size();
		Object[] objects = new Object[size];
		for (int i = 0; i < size; i++) {
			objects[i] = segmentList.get(i);
		}
		return objects;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		int size = segmentList.size();
		Object[] elementData = toArray();
		if (a.length < size) {
			//noinspection unchecked,SuspiciousArrayCast
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		}
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	@Override
	public Spliterator<Segment> spliterator() {
		return segmentList.spliterator();
	}

	@Override
	public int hashCode() {
		int hash = 23;
		hash *= segmentList.hashCode();
		hash *= specification.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof BasicSequence)) { return false; }
		
		BasicSequence object = (BasicSequence) obj;
		return specification.equals(object.specification) && segmentList
			                                                       .equals(object.segmentList);
	}

	@Override
	public boolean removeIf(Predicate<? super Segment> filter) {
		return segmentList.removeIf(filter);
	}

	@Override
	public Stream<Segment> stream() {
		return segmentList.stream();
	}

	@Override
	public Stream<Segment> parallelStream() {
		return segmentList.parallelStream();
	}

	@Override
	public void forEach(Consumer<? super Segment> action) {
		segmentList.forEach(action);
	}

	@Override
	public void replaceAll(UnaryOperator<Segment> operator) {
		segmentList.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super Segment> c) {
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
