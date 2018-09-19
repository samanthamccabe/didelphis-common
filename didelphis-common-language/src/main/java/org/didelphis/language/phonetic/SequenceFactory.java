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

package org.didelphis.language.phonetic;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.Sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class {@code SequenceFactory}
 * 
 * @since 0.0.0
 * @author Samantha Fiona McCabe
 */
@ToString         (of = {"featureMapping", "formatterMode", "reservedStrings"})
@EqualsAndHashCode(of = {"featureMapping", "formatterMode", "reservedStrings"})
@Value
public class SequenceFactory<T> implements Function<String, Sequence<T>> {

	static Map<String, String> DELIMITERS = new HashMap<>();
	static {
		DELIMITERS.put("[", "]");
	}
	
	/* --------------------------------------------------------------------- <*/ 
	FeatureMapping<T>  featureMapping;
	FormatterMode      formatterMode;
	Collection<String> reservedStrings;
	/*> --------------------------------------------------------------------- */

	public SequenceFactory(
			@NonNull FeatureMapping<T> featureMapping,
			@NonNull FormatterMode formatterMode
	) {
		this(featureMapping, new HashSet<>(), formatterMode);
	}

	public SequenceFactory(
			@NonNull FeatureMapping<T> featureMapping,
			@NonNull Collection<String> reservedStrings,
			@NonNull FormatterMode formatterMode
	) {
		/* ----------------------------------------------------------------- <*/
		this.featureMapping  = featureMapping;
		this.reservedStrings = reservedStrings;
		this.formatterMode   = formatterMode;
		/*> ----------------------------------------------------------------- */
	}

	@Override
	public Sequence<T> apply(String word) {
		List<String> keys = new ArrayList<>();
		keys.addAll(reservedStrings);
		keys.addAll(featureMapping.getFeatureMap().keySet());
		keys.sort(SequenceFactory::compare);
		Collection<String> list = formatterMode.split(word, keys, DELIMITERS);
		FeatureModel<T> featureModel = featureMapping.getFeatureModel();
		List<Segment<T>> segments = list.stream()
				.map(this::toSegment)
				.collect(Collectors.toList());
		return new BasicSequence<>(segments, featureModel);
	}

	public void reserve(@NonNull String string) {
		reservedStrings.add(string);
	}

	@NonNull
	public Segment<T> toSegment(@NonNull String string) {
		FeatureSpecification specification = featureMapping.getSpecification();
		FeatureModel<T> featureModel = featureMapping.getFeatureModel();
		if (specification.size() > 0 && string.startsWith("[")) {
			FeatureArray<T> array = featureModel.parseFeatureString(string);
			return new StandardSegment<>(string, array);
		} else {
			return featureMapping.parseSegment(string);
		}
	}

	@NonNull
	public Sequence<T> toSequence(@NonNull String word) {
		return apply(word);
	}

	@NonNull
	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(featureMapping.getSymbols());
		keys.addAll(reservedStrings);
		return keys;
	}

	private static int compare(CharSequence k1, CharSequence k2) {
		return Integer.compare(k2.length(), k1.length());
	}
}
