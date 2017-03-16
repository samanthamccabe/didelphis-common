package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.ModelBearer;
import org.didelphis.common.language.phonetic.segments.Segment;

import java.util.Deque;
import java.util.List;

/**
 * Created by samantha on 1/30/17.
 */
public interface Sequence<N extends Number>
	  extends ModelBearer<N>,
	          Deque<Segment<N>>,
	          List<Segment<N>>,
	          Comparable<Sequence<N>> {

	void add(Sequence<N> sequence);

	void insert(Sequence<N> sequence, int index);

	int indexOf(Sequence<N> target);

	int indexOf(Sequence<N> target, int start);

	Sequence<N> replaceAll(Sequence<N> source, Sequence<N> target);

	boolean contains(Sequence<N> sequence);

	boolean startsWith(Segment<N> segment);

	boolean startsWith(Sequence<N> sequence);

	Sequence<N> remove(int start, int end);

	boolean matches(Sequence<N> sequence);

	Sequence<N> subsequence(int from, int to);

	Sequence<N> subsequence(int from);

	List<Integer> indicesOf(Sequence<N> sequence);
}
