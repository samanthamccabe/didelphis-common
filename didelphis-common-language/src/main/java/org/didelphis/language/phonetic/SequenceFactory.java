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

import org.didelphis.language.enums.FormatterMode;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.model.FeatureMapping;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.ImmutableSegment;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe Date: 11/23/2014
 */
public class SequenceFactory<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(SequenceFactory.class);

	private static final Pattern BACKREFERENCE_PATTERN = Pattern
			.compile("\\$[^$]*\\d+");

	private final FeatureMapping<T> mapping;
	private final FormatterMode formatterMode;
	private final Set<String> reservedStrings;

	private final Segment<T> dotSegment;
	private final Segment<T> borderSegment;

	private final Sequence<T> dotSequence;
	private final Sequence<T> borderSequence;

	public SequenceFactory(FeatureMapping<T> mapping, FormatterMode mode) {
		this(mapping, new HashSet<>(), mode);
	}

	public SequenceFactory(FeatureMapping<T> mapping, Set<String> reserved,
			FormatterMode mode) {
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

	public Segment<T> getDotSegment() {
		return dotSegment;
	}

	public Segment<T> getBorderSegment() {
		return borderSegment;
	}

	public Sequence<T> getDotSequence() {
		return dotSequence;
	}

	public Sequence<T> getBorderSequence() {
		return borderSequence;
	}

	public Segment<T> getSegment(String string) {
		if (!mapping.containsKey("#") && string.equals("#")) {
			return borderSegment;
		} else if (string.equals(".")) {
			return dotSegment;
		} else {
			return mapping.parseSegment(string);
		}
	}

	public Sequence<T> getSequence(String word) {
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

	private static int compare(CharSequence k1, CharSequence k2) {
		return Integer.compare(k2.length(), k1.length());
	}

	@NotNull
	private Segment<T> toSegment(String string) {
		FeatureModel<T> model = mapping.getFeatureModel();
		return string.startsWith("[") && mapping.getSpecification().size() > 0
		       ? new StandardSegment<>(string, model.parseFeatureString(string))
		       : mapping.parseSegment(string);
	}

	public FeatureMapping<T> getFeatureMapping() {
		return mapping;
	}

	@Override
	public String toString() {
		return "SequenceFactory{mapping=" + mapping +
				", formatterMode=" + formatterMode +
				", reservedStrings=" + reservedStrings + '}';
	}

	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(mapping.getSymbols());
		keys.addAll(reservedStrings);
		return keys;
	}
}