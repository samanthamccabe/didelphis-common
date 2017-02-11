package org.didelphis.common.language.phonetic.sequences;

import org.didelphis.common.language.phonetic.Segment;
import org.didelphis.common.language.phonetic.SpecificationBearer;

import java.util.Deque;
import java.util.List;

/**
 * Created by samantha on 1/30/17.
 */
public interface Sequence
	  extends SpecificationBearer,
	          Deque<Segment>,
	          List<Segment>,
	          Comparable<Sequence> {

	void add(Sequence sequence);

	void insert(Sequence sequence, int index);

	int indexOf(Sequence target);

	int indexOf(Sequence target, int start);

	Sequence replaceAll(Sequence source, Sequence target);

	boolean contains(Sequence sequence);

	boolean startsWith(Segment segment);

	boolean startsWith(Sequence sequence);

	BasicSequence remove(int start, int end);

	boolean matches(Sequence sequence);

	Sequence subsequence(int from, int to);

	Sequence subsequence(int from);

	List<Integer> indicesOf(Sequence sequence);
}
