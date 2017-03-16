package org.didelphis.common.language.machines.sequences;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.machines.Expression;
import org.didelphis.common.language.machines.interfaces.MachineParser;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.VariableStore;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by samantha on 2/25/17.
 */
public class SequenceParser<N extends Number> implements MachineParser<Sequence<N>> {

	private final SequenceFactory<N> factory;
	
	private final Map<String, Collection<Sequence<N>>> specials;
	
	private final Sequence<N> epsilon;
	
	public SequenceParser(SequenceFactory<N> factory) {
		this.factory = factory;
		specials = new HashMap<>();
		
		VariableStore variableStore = factory.getVariableStore();
		for (String key : variableStore.getKeys()) {
			Collection<Sequence<N>> values = variableStore.get(key)
					.stream()
					.map(word -> factory.getSequence(word))
					.collect(Collectors.toList());
			specials.put(key, values);	
		}
		epsilon = new BasicSequence<>(factory.getFeatureMapping().getFeatureModel());
	}
	
	@Override
	public Sequence<N> transform(String expression) {
		Sequence<N> sequence = factory.getSequence(expression);
		// Ensure canonical segments are used
		for (int i = 0; i < sequence.size(); i++) {
			String symbol = sequence.get(i).getSymbol();
			if (symbol.equals("#")) {
				sequence.set(i, factory.getBorderSegment());
			} else if (symbol.equals(".")) {
				sequence.set(i, factory.getDotSegment());
			}
		}
		return sequence;
	}

	@Override
	public List<Expression> parseExpression(String expression) {
		FormatterMode formatterMode = factory.getFormatterMode();
		Collection<String> special = factory.getSpecialStrings();
		List<String> strings = formatterMode.split(expression, special);
		List<Expression> list = new ArrayList<>();
		if (!strings.isEmpty()) {
			Expression buffer = new Expression();
			for (String symbol : strings) {
				if ("*?+".contains(symbol)) {
					buffer.setMetacharacter(symbol);
					buffer = updateBuffer(list, buffer);
				} else if (symbol.equals("!")) {
					// first in an expression
					buffer = updateBuffer(list, buffer);
					buffer.setNegative(true);
				} else {
					buffer = updateBuffer(list, buffer);
					buffer.setExpression(symbol);
				}
			}
			if (!buffer.getExpression().isEmpty()) {
				list.add(buffer);
			}
		}
		return list;
	}

	@Override
	public Sequence<N> epsilon() {
		return epsilon;
	}
	
	@Override
	public Map<String, Collection<Sequence<N>>> getSpecials() {
		return Collections.unmodifiableMap(specials);
	}

	@Override
	public Sequence<N> getDot() {
		return factory.getDotSequence();
	}

	@Override
	public int lengthOf(Sequence<N> segments) {
		return segments.size();
	}

	public SequenceFactory<N> getSequenceFactory() {
		return factory;
	}

	private static Expression updateBuffer(Collection<Expression> list, Expression buffer) {
		// Add the contents of buffer if not empty
		if (!buffer.isEmpty()) {
			list.add(buffer);
			return new Expression();
		} else {
			return buffer;
		}
	}

	@Override
	public String toString() {
		return "SequenceParser{"
		       + "factory="
		       + factory
		       + ", specials="
		       + specials
		       + '}';
	}
}
