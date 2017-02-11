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

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.Segment;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.features.StandardFeatureArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Samantha Fiona Morrigan McCabe
 */
public class StandardFeatureModel implements FeatureModel {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(StandardFeatureModel.class);

	public static final FeatureModel EMPTY_MODEL = new StandardFeatureModel();
	
	private final FeatureSpecification specification;
	
	private final Map<String, FeatureArray<Double>> featureMap;
	private final Map<String, FeatureArray<Double>> modifiers;

	// Initializes an empty model; access to this should only be through the
	// EMPTY_MODEL field
	private StandardFeatureModel() {
		specification = FeatureSpecification.EMPTY;
		featureMap = Collections.unmodifiableMap(new HashMap<>());
		modifiers = Collections.unmodifiableMap(new LinkedHashMap<>());
	}

	public StandardFeatureModel(InputStream stream, FormatterMode modeParam) {
		this(new FeatureModelLoader(stream, modeParam));
	}

	public StandardFeatureModel(File file, FormatterMode modeParam) {
		this(new FeatureModelLoader(file, modeParam));
	}

	public StandardFeatureModel(FeatureModelLoader loader) {
		featureMap = Collections.unmodifiableMap(loader.getFeatureMap());
		modifiers = Collections.unmodifiableMap(loader.getDiacritics());
		specification = loader.getSpecification();
	}
	
	@Override
	public String getBestSymbol(FeatureArray<Double> featureArray) {

		FeatureArray<Double> bestFeatures = null;
		String bestSymbol = "";
		double minimum = Double.MAX_VALUE;

		for (Map.Entry<String, FeatureArray<Double>> entry : featureMap.entrySet()) {
			FeatureArray<Double> features = entry.getValue();
			double difference = getDifferenceValue(featureArray, features);
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
	public Collection<Segment> getMatchingSegments(Segment input) {
		Collection<Segment> collection = new ArrayList<>();

		FeatureArray<Double> features = input.getFeatures();

		for (Map.Entry<String, FeatureArray<Double>> entry : featureMap.entrySet()) {
			// This implementation will work but wastes a lot of time on object
			// allocation
			FeatureArray<Double> value = entry.getValue();
			if (value.matches(features)) {
				Segment segment = new Segment(entry.getKey(), value, specification);
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
		String string;
		if (this == EMPTY_MODEL) {
			string = "EMPTY MODEL";
		} else {
			string = "FeatureModel(number.symbols=" + featureMap.size() + ')';
		}
		return string;
	}

	@Override
	public int hashCode() {
		int code = 91;
		code *= featureMap != null ? featureMap.hashCode() : 1;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof StandardFeatureModel)) { return false; }

		StandardFeatureModel other = (StandardFeatureModel) obj;
		boolean diacriticsEquals = modifiers.equals(other.modifiers);
		boolean featureEquals    = featureMap.equals(other.getFeatureMap());
		boolean specEquals = specification.equals(other.specification);
		return specEquals && featureEquals && diacriticsEquals;
	}

	@Override
	public boolean containsKey(String key) {
		return featureMap.containsKey(key);
	}

	@Override
	public Map<String, FeatureArray<Double>> getFeatureMap() {
		//noinspection ReturnOfCollectionOrArrayField - already immutable
		return featureMap;
	}
	
	@Override
	public Map<String, FeatureArray<Double>> getModifiers() {
		//noinspection ReturnOfCollectionOrArrayField - already immutable
		return modifiers;
	}

	@Override
	public FeatureArray<Double> getValue(String key) {
		if (featureMap.containsKey(key)) {
			return new StandardFeatureArray<>(featureMap.get(key));
		} else {
			return new StandardFeatureArray<>(FeatureSpecification.UNDEFINED_VALUE, specification);
		}
	}
	
	@Override
	public Segment getSegment(String string) {

		if (string.startsWith("[")) {
//			new Segment(string,);
			return specification.getSegmentFromFeatures(string);
		}
		
		if (featureMap.isEmpty()) {
			FeatureArray<Double> featureArray = new StandardFeatureArray<>(
				  new ArrayList<Double>(),
				  specification
			);
			return new Segment(string, featureArray, specification);
		}
		
		String best = "";
		for (String key : featureMap.keySet()) {
				if (string.startsWith(key) && key.length() > best.length()) {
					best = key;
				}
			}

			FeatureArray<Double> featureArray = getValue(best);
			for (String s : string.substring(best.length()).split("")) {
				if (modifiers.containsKey(s)) {
					FeatureArray<Double> array = modifiers.get(s);
					featureArray.alter(array);
				}
			}
			return new Segment(string, featureArray, specification);
	}

	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	private static FeatureArray<Double> getDifferenceArray(
		  FeatureArray<Double> left,
		  FeatureArray<Double> right
	) {
		List<Double> list = new ArrayList<>();
		if (left.getSpecification().equals(right.getSpecification())) {
			for (int i = 0; i < left.size(); i++) {
				Double l = left.get(i);
				Double r = right.get(i);
				list.add(getDifference(l, r));
			}
		} else {
			LOGGER.warn("Attempt to compare arrays of differing length! {} vs {}", left, right);
		}
		return new StandardFeatureArray<>(list, left.getSpecification());
	}

	private static double getDifferenceValue(FeatureArray<Double> left,
	                                         FeatureArray<Double> right) {
		double sum = 0.0;
		FeatureArray<Double> differenceArray = getDifferenceArray(left, right);
		for (Double value : differenceArray) {
			sum += value;
		}
		return sum;
	}

	private static Double getDifference(Double a, Double b) {
		if (a == null && b == null) {
			return 0.0;
		} else if (a == null || a.isNaN()) {
			return Math.abs(b);
		} else if (b == null || b.isNaN()) {
			return Math.abs(a);
		} else if (a.equals(b)) {
			return 0.0;
		} else  {
			return Math.abs(a - b);
		}
	}

	private Collection<String> getBestDiacritic(
			FeatureArray<Double> featureArray,
			FeatureArray<Double> bestFeatures,
			double lastMinimum) {
		
		String bestDiacritic = "";
		double minimumDifference = lastMinimum;
		FeatureArray<Double> best = new SparseFeatureArray<>(specification);

		for (Map.Entry<String, FeatureArray<Double>> entry : modifiers.entrySet()) {
			FeatureArray<Double> diacriticFeatures = entry.getValue();

			FeatureArray<Double> compiled = new StandardFeatureArray<>(bestFeatures);
			compiled.alter(diacriticFeatures);

			if (!compiled.equals(bestFeatures)) {
				double difference = getDifferenceValue(compiled, featureArray);
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
