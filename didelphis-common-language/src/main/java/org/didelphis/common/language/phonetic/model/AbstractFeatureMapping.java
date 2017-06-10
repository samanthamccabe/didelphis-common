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

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.language.phonetic.segments.StandardSegment;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.features.StandardFeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author Samantha Fiona McCabe
 */
public abstract class AbstractFeatureMapping<N> implements FeatureMapping<N> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFeatureMapping.class);

	private final FeatureModel<N> featureModel;
	
	private final Map<String, FeatureArray<N>> featureMap;
	private final Map<String, FeatureArray<N>> modifiers;

	protected AbstractFeatureMapping(FeatureModel<N> featureModel,
			Map<String, FeatureArray<N>> featureMap,
			Map<String, FeatureArray<N>> modifiers) {
		this.featureModel = featureModel;
		this.featureMap = Collections.unmodifiableMap(featureMap);
		this.modifiers = Collections.unmodifiableMap(modifiers);
	}

	@NotNull
	@Override
	public String findBestSymbol(@NotNull FeatureArray<N> featureArray) {

		FeatureArray<N> bestFeatures = null;
		String bestSymbol = "";
		double minimum = Double.MAX_VALUE;

		for (Entry<String, FeatureArray<N>> entry : featureMap.entrySet()) {
			FeatureArray<N> features = entry.getValue();
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
	public Map<String, FeatureArray<N>> getFeatureMap() {
		return featureMap;
	}
	
	@NotNull
	@Override
	public Map<String, FeatureArray<N>> getModifiers() {
		return modifiers;
	}

	@NotNull
	@Override
	public FeatureArray<N> getFeatureArray(String key) {
		return featureMap.containsKey(key) 
				? new StandardFeatureArray<>(featureMap.get(key)) 
				: new SparseFeatureArray<>(featureModel);
	}
	
	@NotNull
	@Override
	public Segment<N> parseSegment(@NotNull String string) {
		if (string.startsWith("[")) {
			return new StandardSegment<>(string, featureModel.parseFeatureString(string), featureModel);
		}
		
		if (featureMap.isEmpty()) {
			FeatureArray<N> featureArray = new StandardFeatureArray<>(
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

			FeatureArray<N> featureArray = getFeatureArray(best);
			for (String s : string.substring(best.length()).split("")) {
				if (modifiers.containsKey(s)) {
					FeatureArray<N> array = modifiers.get(s);
					featureArray.alter(array);
				}
			}
			return new StandardSegment<>(string, featureArray, featureModel);
	}

	@Override
	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}
	
	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

	private  double totalDifference(FeatureArray<N> left,
			FeatureArray<N> right) {
		if (left.size() != right.size()) {
			throw new IllegalArgumentException("Cannot compare feature arrays" +
					" of different sizes! left: " + left.size() + " right: " +
					right.size());
		}
		return IntStream.range(0, left.size())
				.mapToDouble(i -> difference(left.get(i), right.get(i)))
				.sum();
	}

	protected abstract double difference(N x, N y);

	private Collection<String> getBestDiacritic(
			FeatureArray<N> featureArray,
			FeatureArray<N> bestFeatures,
			double lastMinimum) {
		
		String bestDiacritic = "";
		double minimumDifference = lastMinimum;
		FeatureArray<N> best = new SparseFeatureArray<>(featureModel);

		for (Entry<String, FeatureArray<N>> entry : modifiers.entrySet()) {
			FeatureArray<N> diacriticFeatures = entry.getValue();

			FeatureArray<N> compiled = new StandardFeatureArray<>(bestFeatures);
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
