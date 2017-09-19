/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.machines.sequences;

import lombok.NonNull;
import org.didelphis.language.machines.Expression;
import org.didelphis.language.machines.interfaces.MachineParser;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class {@code SequenceParser}
 *
 * @param <T> 
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-25
 * @since 0.1.0
 */
public class SequenceParser<T> implements MachineParser<Sequence<T>> {

	private final SequenceFactory<T> factory;

	private final MultiMap<String, Sequence<T>> specials;

	private final Sequence<T> epsilon;

	public SequenceParser(@NonNull SequenceFactory<T> factory) {
		this(factory, new GeneralMultiMap<>());
	}

	public SequenceParser(
			@NonNull SequenceFactory<T> factory,
			@NonNull MultiMap<String, Sequence<T>> specials
	) {
		this.factory = factory;
		this.specials = specials;
		// Generate epsilon / lambda symbol
		FeatureModel<T> model = factory.getFeatureMapping().getFeatureModel();
		FeatureArray<T> array = new SparseFeatureArray<>(model);
		Segment<T> segment = new StandardSegment<>("\uD835\uDF06", array);
		epsilon = new BasicSequence<>(segment);
	}

	@Override
	public Sequence<T> transform(String expression) {
		return factory.toSequence(expression);
	}

	@NonNull
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

	@NonNull
	@Override
	public Sequence<T> epsilon() {
		return epsilon;
	}

	@NonNull
	@Override
	public Map<String, Collection<Sequence<T>>> getSpecials() {
		return Collections.unmodifiableMap(specials);
	}

	@Override
	@NonNull
	public @NotNull Sequence<T> getDot() {
		return factory.getDotSequence();
	}

	@Override
	public int lengthOf(@NonNull Sequence<T> segments) {
		return segments.size();
	}

	@NonNull
	@Override
	public String toString() {
		return "SequenceParser{"
				+ "factory="
				+ factory
				+ ", specials="
				+ specials
				+ '}';
	}

	@NonNull
	public SequenceFactory<T> getSequenceFactory() {
		return factory;
	}

	@NonNull
	private static Expression updateBuffer(
			@NonNull Collection<Expression> list, @NonNull Expression buffer
	) {
		// Add the contents of buffer if not empty
		if (buffer.isEmpty()) {
			return buffer;
		} else {
			list.add(buffer);
			return new Expression();
		}
	}
}
