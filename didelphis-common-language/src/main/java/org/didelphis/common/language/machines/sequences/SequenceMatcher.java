package org.didelphis.common.language.machines.sequences;

import org.didelphis.common.language.machines.interfaces.MachineMatcher;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by samantha on 2/25/17.
 */
public class SequenceMatcher<N extends Number>
		implements MachineMatcher<Sequence<N>> {

	private final SequenceParser<N> parser;

	public SequenceMatcher(SequenceParser<N> parser) {
		this.parser = parser;
	}

	@Override
	public int match(Sequence<N> target, Sequence<N> arc, int index) {

		Map<String, Collection<Sequence<N>>> specials = parser.getSpecials();

		SequenceFactory<N> factory = parser.getSequenceFactory();
		
		Sequence<N> tail = target.subsequence(index);

		if (tail.isEmpty() && Objects.equals(arc, factory.getBorderSequence())) {
			return index + 1;
		}

		if (Objects.equals(arc, parser.epsilon())) {
			return index;
		}
		
		if (specials.containsKey(arc.toString())) {
			for (Sequence<N> special : specials.get(arc.toString())) {
				if (tail.startsWith(special)) {
					return index + special.size();
				}
			}
			return -1;
		}
		
		if (tail.startsWith(arc)) {
			// Should work for both cases which have the same behavior
			return index + arc.size();
		}
		
		if (arc.equals(factory.getDotSequence())) {
			if (!tail.isEmpty() && !tail.startsWith(factory.getBorderSegment())) {
				return index + arc.size();
			}
		}
		// Else: the pattern fails to match
		return -1;
	}

	@Override
	public String toString() {
		return "SequenceMatcher{" + "parser=" + parser + '}';
	}
}
