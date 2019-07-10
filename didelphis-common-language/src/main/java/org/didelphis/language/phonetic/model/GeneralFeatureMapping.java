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

package org.didelphis.language.phonetic.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.Regex;
import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.features.StandardFeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.segments.UndefinedSegment;
import org.didelphis.utilities.Sort;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 */
@ToString
@EqualsAndHashCode
public class GeneralFeatureMapping<T> implements FeatureMapping<T> {

	private static final Regex BINDER = new Regex("[͜-͢]");

	private final FeatureSpecification specification;
	private final FeatureModel<T> featureModel;
	
	private final Map<String, FeatureArray<T>> featureMap;
	private final Map<String, FeatureArray<T>> modifiers;
	private final List<String> orderedKeys;

	public GeneralFeatureMapping(
			@NonNull FeatureModel<T> featureModel,
			@NonNull Map<String, FeatureArray<T>> featureMap,
			@NonNull Map<String, FeatureArray<T>> modifiers
	) {
		specification = featureModel.getSpecification();
		this.featureModel = featureModel;
		this.featureMap = Collections.unmodifiableMap(featureMap);
		this.modifiers = Collections.unmodifiableMap(modifiers);

		orderedKeys = new LinkedList<>(featureMap.keySet());
		Sort.quicksort(orderedKeys, (s1, s2) -> {
			int x = Normalizer.normalize(s1, Normalizer.Form.NFD).length();
			int y = Normalizer.normalize(s2, Normalizer.Form.NFD).length();
			return -1 * ((x < y) ? -1 : ((x == y) ? 0 : 1));
		});
	}

	@NonNull
	@Override
	public String findBestSymbol(@NonNull FeatureArray<T> featureArray) {

		FeatureType<T> type = featureModel.getFeatureType();
		
		String bestSymbol = "";
		double minimum = Double.MAX_VALUE;

		FeatureArray<T> bestFeatures = null;
		for (String key : orderedKeys) {
			FeatureArray<T> features = featureMap.get(key);
			double difference = type.difference(featureArray, features);
			if (difference < minimum) {
				bestSymbol = key;
				minimum = difference;
				bestFeatures = features;
			}
		}

		String sb = "";
		if (minimum > 0.0 && bestFeatures != null) {
			Collection<String> collection = getBestDiacritic(
					featureArray, 
					bestFeatures, 
					Double.MAX_VALUE
			);
			sb = modifiers.keySet()
					.stream()
					.filter(collection::contains)
					.collect(Collectors.joining());
		}
		return bestSymbol + sb;
	}

	@NonNull
	@Override
	public Set<String> getSymbols() {
		return featureMap.keySet();
	}
	
	@Override
	public boolean containsKey(@NonNull String key) {
		key = Normalizer.normalize(key, Normalizer.Form.NFD);
		return featureMap.containsKey(key);
	}

	@NonNull
	@Override
	public Map<String, FeatureArray<T>> getFeatureMap() {
		return featureMap;
	}
	
	@NonNull
	@Override
	public Map<String, FeatureArray<T>> getModifiers() {
		return modifiers;
	}

	@NonNull
	@Override
	public FeatureArray<T> getFeatureArray(@NonNull String key) {
		key = Normalizer.normalize(key, Normalizer.Form.NFD);
		return featureMap.containsKey(key) 
				? new StandardFeatureArray<>(featureMap.get(key)) 
				: new SparseFeatureArray<>(featureModel);
	}

	@NonNull
	@Override
	public Segment<T> parseSegment(@NonNull String string) {
		string = Normalizer.normalize(string, Normalizer.Form.NFD);

		if (featureMap.isEmpty()) {
			FeatureArray<T> array = new EmptyFeatureArray<>(featureModel);
			return new StandardSegment<>(string, array);
		}

		if (string.startsWith("[")) {
			FeatureArray<T> array = featureModel.parseFeatureString(string);
			return new StandardSegment<>(string, array);
		}

		FeatureArray<T> modifierArray = new SparseFeatureArray<>(featureModel);
		int index = 0;
		for (int i = string.length() - 1; i >= 0; i--) {
			String substring = string.substring(i, i + 1);
			if (modifiers.containsKey(substring)) {
				FeatureArray<T> array = modifiers.get(substring);
				modifierArray.alter(array);
				index = i;
			}
		}

		String substring = index <= 0 ? string : string.substring(0, index);

		String best = "";
		for (String key : orderedKeys) {
			String s1 = BINDER.replace(substring, "");
			String s2 = BINDER.replace(key, "");
			if (s1.startsWith(s2) && key.length() > best.length()) {
				best = key;
			}
		}

		if ((best.isEmpty())) {
			return new UndefinedSegment<>(string, featureModel);
		}
		
		FeatureArray<T> featureArray = getFeatureArray(best);
		featureArray.alter(modifierArray);
		return new StandardSegment<>(string, featureArray);
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}
	
	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@NonNull
	private Collection<String> getBestDiacritic(
			@NonNull FeatureArray<T> featureArray,
			@NonNull FeatureArray<T> bestFeatures,
			double lastMinimum
	) {

		FeatureType<T> type = featureModel.getFeatureType();

		String bestDiacritic = "";
		double minimumDifference = lastMinimum;
		FeatureArray<T> best = new SparseFeatureArray<>(featureModel);

		for (Map.Entry<String, FeatureArray<T>> entry : modifiers.entrySet()) {
			FeatureArray<T> diacriticFeatures = entry.getValue();
			FeatureArray<T> compiled = new StandardFeatureArray<>(bestFeatures);
			compiled.alter(diacriticFeatures);

			if (!compiled.equals(bestFeatures)) {
				double difference = type.difference(compiled, featureArray);
				if (difference < minimumDifference) {
					minimumDifference = difference;
					bestDiacritic = entry.getKey();
					best = compiled;
				}
			}
		}

		Collection<String> diacriticList = new ArrayList<>();
		if (minimumDifference > 0.0 && minimumDifference < lastMinimum) {
			diacriticList.add(bestDiacritic);
			Collection<String> diacritics = getBestDiacritic(
					featureArray,
					best,
					minimumDifference
			);
			diacriticList.addAll(diacritics);
		} else {
			diacriticList.add(bestDiacritic);
		}
		return diacriticList;
	}
}
