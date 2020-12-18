/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
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
import org.didelphis.language.phonetic.sequences.PhoneticSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.utilities.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class {@code SequenceFactory}
 *
 * @since 0.0.0
 */
@ToString         (of = {"featureMapping", "formatterMode", "reservedStrings"})
@EqualsAndHashCode(of = {"featureMapping", "formatterMode", "reservedStrings"})
@Value
public class SequenceFactory {

	private static final Map<String, String> DELIMITERS = new HashMap<>();
	static {
		DELIMITERS.put("[", "]");
	}

	FeatureMapping  featureMapping;
	FormatterMode      formatterMode;
	Collection<String> reservedStrings;

	public SequenceFactory(
			@NonNull FeatureMapping featureMapping,
			@NonNull FormatterMode formatterMode
	) {
		this(featureMapping, new HashSet<>(), formatterMode);
	}

	public SequenceFactory(
			@NonNull FeatureMapping featureMapping,
			@NonNull Collection<String> reservedStrings,
			@NonNull FormatterMode formatterMode
	) {
		/* ----------------------------------------------------------------- <*/
		this.featureMapping  = featureMapping;
		this.reservedStrings = reservedStrings;
		this.formatterMode   = formatterMode;
		/*> ----------------------------------------------------------------- */
	}

	public void reserve(@NonNull String reserved) {
		reservedStrings.add(reserved);
	}

	@NonNull
	public Segment toSegment(@NonNull String string) {
		FeatureSpecification specification = featureMapping.getSpecification();
		FeatureModel featureModel = featureMapping.getFeatureModel();
		if (specification.size() > 0 && string.startsWith("[")) {
			FeatureArray array = featureModel.parseFeatureString(string);
			return new StandardSegment(string, array);
		} else {
			return featureMapping.parseSegment(string);
		}
	}

	@NonNull
	public Sequence toSequence(@NonNull String word) {
		List<String> keys = new ArrayList<>();
		keys.addAll(reservedStrings);
		keys.addAll(featureMapping.getFeatureMap().keySet());
		Sort.quicksort(keys, SequenceFactory::compare);
		Collection<String> list = formatterMode.split(word, keys, DELIMITERS);
		FeatureModel featureModel = featureMapping.getFeatureModel();
		List<Segment> segments = list.stream()
				.map(this::toSegment)
				.collect(Collectors.toList());
		return new PhoneticSequence(segments, featureModel);	}

	@NonNull
	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(featureMapping.getSymbols());
		keys.addAll(reservedStrings);
		return keys;
	}

	private static int compare(CharSequence k1, CharSequence k2) {
		int x = k1.length();
		int y = k2.length();
		return Integer.compare(x, y);
	}
}
