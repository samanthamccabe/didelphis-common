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

package org.didelphis.language.automata.parsers;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.UndefinedSegment;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.jetbrains.annotations.Contract;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class {@code SequenceParser}
 *
 * @param <T> the feature type used by language objects supported by a given
 * instance of this parser.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-25
 * @since 0.1.0
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SequenceParser<T> extends AbstractDidelphisParser<Sequence<T>> {

	SequenceFactory<T> factory;
	MultiMap<String, Sequence<T>> specials;
	
	Sequence<T> wordStart;
	Sequence<T> wordEnd;
	Sequence<T> epsilon;
	Sequence<T> dot;

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
		wordStart = immutable("#[", model);
		wordEnd   = immutable("]#", model);
		epsilon   = immutable("𝜆",  model);
		dot       = immutable(".",  model);
	}
	
	@Override
	public Sequence<T> getWordStart() {
		return wordStart;
	}

	@Override
	public Sequence<T> getWordEnd() {
		return wordEnd;
	}

	@Override
	public Sequence<T> transform(String expression) {
		if (expression.equals("#[")) return wordStart;
		if (expression.equals("]#")) return wordEnd;
		if (expression.equals(".")) return dot;
		if (expression.isEmpty()) return epsilon;
		return factory.toSequence(expression);
	}

	@NonNull
	@Override
	public Sequence<T> epsilon() {
		return epsilon;
	}

	@NonNull
	@Override
	public MultiMap<String, Sequence<T>> getSpecials() {
		return specials;
	}

	@NonNull
	@Override
	public Sequence<T> getDot() {
		return dot;
	}

	@Override
	public int lengthOf(@NonNull Sequence<T> segments) {
		return segments.size();
	}

	@Override
	@NonNull
	public List<String> split(String substring) {
		FormatterMode formatter = factory.getFormatterMode();
		Set<String> set = new HashSet<>();
		set.addAll(factory.getSpecialStrings());
		set.addAll(specials.keys());
		return formatter.split(substring, set);
	}

	@NonNull
	@Contract ("_, _ -> new")
	private static <T> Sequence<T> immutable(
			@NonNull String symbol,
			@NonNull FeatureModel<T> model
	) {
		// Undefined segments can only match when the symbol matches
		return new ImmutableSequence<>(new UndefinedSegment<>(symbol, model));
	}
}
