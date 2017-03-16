/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.language.phonetic.segments.StandardSegment;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.features.StandardFeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Samantha Fiona Morrigan McCabe
 */
public abstract class AbstractFeatureMapping<N extends Number>
		implements FeatureMapping<N> {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(AbstractFeatureMapping.class);

//	public static final FeatureModel<?> EMPTY_MODEL = new AbstractFeatureMapping();
	
	private final FeatureModel<N> featureModel;
	
	private final Map<String, FeatureArray<N>> featureMap;
	private final Map<String, FeatureArray<N>> modifiers;

	protected AbstractFeatureMapping(FeatureModel<N> featureModel,
			Map<String, FeatureArray<N>> featureMap,
			Map<String, FeatureArray<N>> modifiers) {
		this.featureModel = featureModel;
		this.featureMap = featureMap;
		this.modifiers = modifiers;
	}

	@Override
	public String findBestSymbol(FeatureArray<N> featureArray) {

		FeatureArray<N> bestFeatures = null;
		String bestSymbol = "";
		double minimum = Double.MAX_VALUE;

		for (Map.Entry<String, FeatureArray<N>> entry : featureMap.entrySet()) {
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

	// Return a list of all segments g such that matches.matches(input) is true
	public Collection<Segment<N>> getMatchingSegments(Segment<N> input) {
		Collection<Segment<N>> collection = new ArrayList<>();

		FeatureArray<N> features = input.getFeatures();

		for (Map.Entry<String, FeatureArray<N>> entry : featureMap.entrySet()) {
			// This implementation will work but wastes a lot of time on object
			// allocation
			FeatureArray<N> value = entry.getValue();
			if (value.matches(features)) {
				//TODO: was "specification" rather than "this"
				Segment<N> segment = new StandardSegment<>(entry.getKey(), value, featureModel);
				collection.add(segment);
			}
		}

		return collection;
	}

	@Override
	public Set<String> getSymbols() {
		return featureMap.keySet();
	}

	@Override
	public String toString() {
		return "FeatureModel(number.symbols=" + featureMap.size() + ')';
	}
	
	@Override
	public boolean containsKey(String key) {
		return featureMap.containsKey(key);
	}

	@Override
	public Map<String, FeatureArray<N>> getFeatureMap() {
		//noinspection ReturnOfCollectionOrArrayField - already immutable
		return featureMap;
	}
	
	@Override
	public Map<String, FeatureArray<N>> getModifiers() {
		//noinspection ReturnOfCollectionOrArrayField - already immutable
		return modifiers;
	}

	@Override
	public FeatureArray<N> getFeatureArray(String key) {
		return featureMap.containsKey(key) 
				? new StandardFeatureArray<>(featureMap.get(key)) 
				: null;
	}
	
	@Override
	public Segment<N> getSegment(String string) {

		if (string.startsWith("[")) {
			return new StandardSegment<>(string, featureModel.parseFeatureString(string), featureModel);
		}
		
		if (featureMap.isEmpty()) {
			FeatureArray<N> featureArray = new StandardFeatureArray<>(
				  new ArrayList<N>(),
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

	protected abstract double totalDifference(FeatureArray<N> left, FeatureArray<N> right);
	
	protected abstract double difference(N x, N y);

	private Collection<String> getBestDiacritic(
			FeatureArray<N> featureArray,
			FeatureArray<N> bestFeatures,
			double lastMinimum) {
		
		String bestDiacritic = "";
		double minimumDifference = lastMinimum;
		FeatureArray<N> best = new SparseFeatureArray<>(featureModel);

		for (Map.Entry<String, FeatureArray<N>> entry : modifiers.entrySet()) {
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
