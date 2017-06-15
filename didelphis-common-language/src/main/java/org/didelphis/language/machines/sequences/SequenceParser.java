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

package org.didelphis.language.machines.sequences;

import org.didelphis.language.enums.FormatterMode;
import org.didelphis.language.machines.Expression;
import org.didelphis.language.machines.interfaces.MachineParser;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.VariableStore;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;

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
public class SequenceParser<T> implements MachineParser<Sequence<T>> {

	private final SequenceFactory<T> factory;
	
	private final Map<String, Collection<Sequence<T>>> specials;
	
	private final Sequence<T> epsilon;
	
	public SequenceParser(SequenceFactory<T> factory) {
		this.factory = factory;
		specials = new HashMap<>();
		
		VariableStore variableStore = factory.getVariableStore();
		for (String key : variableStore.getKeys()) {
			Collection<Sequence<T>> values = variableStore.get(key)
					.stream()
					.map(factory::getSequence)
					.collect(Collectors.toList());
			specials.put(key, values);
		}
		
		// Generate epsilon / lambda symbol
		FeatureModel<T> model = factory.getFeatureMapping().getFeatureModel();
		FeatureArray<T> array = new SparseFeatureArray<>(model);
		Segment<T> segment = new StandardSegment<>("\uD835\uDF06", array, model);
		epsilon = new BasicSequence<>(segment);
	}
	
	@Override
	public Sequence<T> transform(String expression) {
		Sequence<T> sequence = factory.getSequence(expression);
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
	public Sequence<T> epsilon() {
		return epsilon;
	}
	
	@Override
	public Map<String, Collection<Sequence<T>>> getSpecials() {
		return Collections.unmodifiableMap(specials);
	}

	@Override
	public Sequence<T> getDot() {
		return factory.getDotSequence();
	}

	@Override
	public int lengthOf(Sequence<T> segments) {
		return segments.size();
	}

	public SequenceFactory<T> getSequenceFactory() {
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
