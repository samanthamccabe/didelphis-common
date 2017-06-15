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

package org.didelphis.language.phonetic.model;

import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.features.StandardFeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author Samantha Fiona McCabe
 */
public class GeneralFeatureMapping<T> implements FeatureMapping<T> {

	private static final Logger LOG = LoggerFactory.getLogger(GeneralFeatureMapping.class);

	private final FeatureModel<T> featureModel;
	
	private final Map<String, FeatureArray<T>> featureMap;
	private final Map<String, FeatureArray<T>> modifiers;

	public GeneralFeatureMapping(FeatureModel<T> featureModel,
			Map<String, FeatureArray<T>> featureMap,
			Map<String, FeatureArray<T>> modifiers) {
		this.featureModel = featureModel;
		this.featureMap = Collections.unmodifiableMap(featureMap);
		this.modifiers = Collections.unmodifiableMap(modifiers);
	}

	@NotNull
	@Override
	public String findBestSymbol(@NotNull FeatureArray<T> featureArray) {

		FeatureArray<T> bestFeatures = null;
		String bestSymbol = "";
		double minimum = Double.MAX_VALUE;

		for (Entry<String, FeatureArray<T>> entry : featureMap.entrySet()) {
			FeatureArray<T> features = entry.getValue();
			double difference = totalDifference(featureArray, features);
			if (difference < minimum) {
				bestSymbol = entry.getKey();
				minimum = difference;
				bestFeatures = features;
			}
		}

		StringBuilder sb = new StringBuilder();
		if (minimum > 0.0) {
			Collection<String> collection = getBestDiacritic(featureArray, bestFeatures, Double.MAX_VALUE);
			for (String diacritic : modifiers.keySet()) {
				if (collection.contains(diacritic)) {
					sb.append(diacritic);
				}
			}
		}
		return bestSymbol + sb;
	}

	@NotNull
	@Override
	public Set<String> getSymbols() {
		return featureMap.keySet();
	}
	
	@Override
	public boolean containsKey(@NotNull String key) {
		return featureMap.containsKey(key);
	}

	@NotNull
	@Override
	public Map<String, FeatureArray<T>> getFeatureMap() {
		return featureMap;
	}
	
	@NotNull
	@Override
	public Map<String, FeatureArray<T>> getModifiers() {
		return modifiers;
	}

	@NotNull
	@Override
	public FeatureArray<T> getFeatureArray(String key) {
		return featureMap.containsKey(key) 
				? new StandardFeatureArray<>(featureMap.get(key)) 
				: new SparseFeatureArray<>(featureModel);
	}
	
	@NotNull
	@Override
	public Segment<T> parseSegment(@NotNull String string) {
		if (string.startsWith("[")) {
			return new StandardSegment<>(string, featureModel.parseFeatureString(string), featureModel);
		}
		
		if (featureMap.isEmpty()) {
			FeatureArray<T> featureArray = new StandardFeatureArray<>(
					new ArrayList<>(),
				  featureModel
			);
			return new StandardSegment<>(string, featureArray, featureModel);
		}
		
		String best = "";
		for (String key : featureMap.keySet()) {
				if (string.startsWith(key) && key.length() > best.length()) {
					best = key;
				}
			}

			FeatureArray<T> featureArray = getFeatureArray(best);
			for (String s : string.substring(best.length()).split("")) {
				if (modifiers.containsKey(s)) {
					FeatureArray<T> array = modifiers.get(s);
					featureArray.alter(array);
				}
			}
			return new StandardSegment<>(string, featureArray, featureModel);
	}

	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}
	
	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureModel, featureMap, modifiers);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final GeneralFeatureMapping other = (GeneralFeatureMapping) obj;
		return Objects.equals(this.featureModel, other.featureModel) &&
				Objects.equals(this.featureMap, other.featureMap) &&
				Objects.equals(this.modifiers, other.modifiers);
	}

	private double totalDifference(FeatureArray<T> left,
			FeatureArray<T> right) {
		if (left.size() != right.size()) {
			throw new IllegalArgumentException("Cannot compare feature arrays" +
					" of different sizes! left: " + left.size() + " right: " +
					right.size());
		}
		FeatureType<T> featureType = featureModel.getFeatureType();
		return IntStream.range(0, left.size())
				.mapToDouble(i -> featureType.difference(left.get(i), right.get(i)))
				.sum();
	}

	private Collection<String> getBestDiacritic(
			FeatureArray<T> featureArray,
			FeatureArray<T> bestFeatures,
			double lastMinimum) {
		
		String bestDiacritic = "";
		double minimumDifference = lastMinimum;
		FeatureArray<T> best = new SparseFeatureArray<>(featureModel);

		for (Entry<String, FeatureArray<T>> entry : modifiers.entrySet()) {
			FeatureArray<T> diacriticFeatures = entry.getValue();

			FeatureArray<T> compiled = new StandardFeatureArray<>(bestFeatures);
			compiled.alter(diacriticFeatures);

			if (!compiled.equals(bestFeatures)) {
				double difference = totalDifference(compiled, featureArray);
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
			diacriticList.addAll(getBestDiacritic(featureArray, best, minimumDifference));
		} else {
			diacriticList.add(bestDiacritic);
		}
		return diacriticList;
	}
}
