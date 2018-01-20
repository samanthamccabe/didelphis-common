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

package org.didelphis.language.phonetic.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.didelphis.language.phonetic.features.*;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import lombok.NonNull;
import org.didelphis.language.phonetic.segments.UndefinedSegment;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe
 */
@ToString
@EqualsAndHashCode
public class GeneralFeatureMapping<T> implements FeatureMapping<T> {

	private static final Pattern COMPILE = Pattern.compile("");
	
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

		orderedKeys = new ArrayList<>(featureMap.keySet());
		orderedKeys.sort(GeneralFeatureMapping::compare);
	}

	private static int compare(CharSequence s1, CharSequence s2) {
		s1 = Normalizer.normalize(s1, Normalizer.Form.NFD);
		s2 = Normalizer.normalize(s2, Normalizer.Form.NFD);
		return -1 * Integer.compare(s1.length(), s2.length());
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
		if (minimum > 0.0) {
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
		String best = "";
		for (String key : orderedKeys) {
			if (string.startsWith(key) && key.length() > best.length()) {
				best = key;
			}
		}
		
		if (best.isEmpty()) {
			return new UndefinedSegment<>(string, featureModel);
		}
		
		FeatureArray<T> featureArray = getFeatureArray(best);
		for (String s : COMPILE.split(string.substring(best.length()))) {
			if (modifiers.containsKey(s)) {
				FeatureArray<T> array = modifiers.get(s);
				featureArray.alter(array);
			}
		}
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
