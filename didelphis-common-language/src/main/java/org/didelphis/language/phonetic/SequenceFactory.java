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

package org.didelphis.language.phonetic;

import lombok.Getter;
import lombok.ToString;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.language.phonetic.segments.ImmutableSegment;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe
	 * @date 11/23/2014
 */
@ToString(includeFieldNames = true, of = {
		"mapping",
		"formatterMode",
		"reservedStrings"
})
public class SequenceFactory<T> implements Function<String, Sequence<T>> {

	private final FeatureMapping<T> mapping;
	private final FormatterMode formatterMode;
	private final Collection<String> reservedStrings;

	// <|-----------------------------------------------------------------------
	@Getter private final Segment<T>  dotSegment;
	@Getter private final Segment<T>  borderSegment;
	
	@Getter private final Sequence<T> dotSequence;
	@Getter private final Sequence<T> borderSequence;
	// -----------------------------------------------------------------------|>

	public SequenceFactory(
			@NotNull FeatureMapping<T> mapping, @NotNull FormatterMode mode
	) {
		this(mapping, Collections.emptySet(), mode);
	}

	public SequenceFactory(
			@NotNull FeatureMapping<T> mapping,
			@NotNull Collection<String> reserved,
			@NotNull FormatterMode mode
	) {
		this.mapping = mapping;
		reservedStrings = reserved;
		formatterMode = mode;

		FeatureModel<T> model = this.mapping.getFeatureModel();
		FeatureArray<T> sparseArray = new SparseFeatureArray<>(model);

		dotSegment = new ImmutableSegment<>(".", sparseArray);
		borderSegment = new ImmutableSegment<>("#", sparseArray);

		dotSequence = new ImmutableSequence<>(dotSegment);
		borderSequence = new ImmutableSequence<>(borderSegment);
	}

	public FormatterMode getFormatterMode() {
		return formatterMode;
	}

	public void reserve(String string) {
		reservedStrings.add(string);
	}

	@NotNull
	public Segment<T> toSegment(@NotNull String string) {
		FeatureSpecification specification = mapping.getSpecification();
		FeatureModel<T> featureModel = mapping.getFeatureModel();
		if (!mapping.containsKey("#") && string.equals("#")) {
			return borderSegment;
		} else if (string.equals(".")) {
			return dotSegment;
		} else if (specification.size() > 0 && string.startsWith("[")) {
			FeatureArray<T> array = featureModel.parseFeatureString(string);
			return new StandardSegment<>(mapping.findBestSymbol(array), array);
		} else {
			return mapping.parseSegment(string);
		}
	}

	@NotNull
	public Sequence<T> toSequence(@NotNull String word) {
		return apply(word);
	}

	@Override
	public Sequence<T> apply(String word) {
		if (word.equals("#")) {
			return borderSequence;
		} else if (word.equals(".")) {
			return dotSequence;
		} else {
			List<String> keys = new ArrayList<>();
			keys.addAll(reservedStrings);
			keys.addAll(mapping.getFeatureMap().keySet());
			keys.sort(SequenceFactory::compare);
			List<String> list = formatterMode.split(word, keys);
			FeatureModel<T> featureModel = mapping.getFeatureModel();
			List<Segment<T>> segments = list.stream()
					.map(this::toSegment)
					.collect(Collectors.toList());
			return new BasicSequence<>(segments, featureModel);
		}
	}

	private static int compare(
			@NotNull CharSequence k1, @NotNull CharSequence k2
	) {
		return Integer.compare(k2.length(), k1.length());
	}

	public FeatureMapping<T> getFeatureMapping() {
		return mapping;
	}

	@NotNull
	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(mapping.getSymbols());
		keys.addAll(reservedStrings);
		return keys;
	}
}
