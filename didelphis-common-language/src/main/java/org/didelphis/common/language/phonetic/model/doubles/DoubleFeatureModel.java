/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
 * * This program is free software: you can redistribute it and/or modify * it
 * under the terms of the GNU General Public License as published by * the Free
 * Software Foundation, either version 3 of the License, or * (at your option)
 * any later version. * * This program is distributed in the hope that it will
 * be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General
 * Public License for more details. * * You should have received a copy of the
 * GNU General Public License * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>. *
 ******************************************************************************/

package org.didelphis.common.language.phonetic.model.doubles;

import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.FeatureType;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe Created: 7/2/2016
 */
public final class DoubleFeatureModel implements FeatureModel<Double> {

	private static final Logger LOG = LoggerFactory.getLogger(DoubleFeatureModel.class);

	private static final String VALUE = "(-?\\d|[A-Zα-ω]+)";
	private static final String NAME = "(\\w+)";
	private static final String ASSIGN = "([=:><])";

	private static final Pattern VALUE_PATTERN = Pattern.compile(VALUE + ASSIGN + NAME);
	private static final Pattern BINARY_PATTERN = Pattern.compile("([+\\-−])" + NAME);
	private static final Pattern FEATURE_PATTERN = Pattern.compile("[,;]\\s*|\\s+");
	private static final Pattern FANCY_PATTERN = Pattern.compile("−");

	private final FeatureSpecification specification;
	private final List<Constraint<Double>> constraints;
	private final Map<String, FeatureArray<Double>> aliases;

	public  DoubleFeatureModel(FeatureSpecification specification,
			List<Constraint<Double>> constraints,
			Map<String, FeatureArray<Double>> aliases) {
		this.specification = specification;
		this.constraints = constraints;
		this.aliases = aliases;
	}
	
	@Override
	public String toString() {
		return "DoubleFeatureSpecification{" + size() + '}';
	}

	@Override
	public int size() {
		return specification.size();
	}

	@Override
	public Map<String, Integer> getFeatureIndices() {
		return specification.getFeatureIndices();
	}

	@Override
	public int getIndex(String featureName) {
		return specification.getIndex(featureName);
	}

	@Override
	public List<String> getFeatureNames() {
		return specification.getFeatureNames();
	}

	@Override
	public List<FeatureType> getFeatureTypes() {
		return specification.getFeatureTypes();
	}

	@Override
	public List<Constraint<Double>> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	@Override
	public FeatureArray<Double> parseFeatureString(String string) {
		return getFeatureArray(string);
	}

	private static Integer retrieveIndex(String label, String features,
			Map<String, Integer> names) {
		if (names.containsKey(label)) {
			return names.get(label);
		}
		throw new ParseException("Invalid feature label", features);
	}

	private FeatureArray<Double> getFeatureArray(String features) {
		String string = FANCY_PATTERN
				.matcher(features.substring(1, features.length() - 1))
				.replaceAll(Matcher.quoteReplacement("-"));

		FeatureArray<Double> arr = new SparseFeatureArray<>(this);
		for (String element : FEATURE_PATTERN.split(string)) {
			Matcher valueMatcher = VALUE_PATTERN.matcher(element);
			Matcher binaryMatcher = BINARY_PATTERN.matcher(element);

			if (aliases.containsKey(element)) {
				arr.alter(aliases.get(element));
			} else if (valueMatcher.matches()) {
				String featureName = valueMatcher.group(3);
				String assignment = valueMatcher.group(2);
				String featureValue = valueMatcher.group(1);
				Integer value =
						retrieveIndex(featureName, features, specification.getFeatureIndices());
				arr.set(value, Double.valueOf(featureValue));
			} else if (binaryMatcher.matches()) {
				String featureName = binaryMatcher.group(2);
				String featureValue = binaryMatcher.group(1);
				Integer value = retrieveIndex(featureName, features, getFeatureIndices());
				arr.set(value, featureValue.equals("+") ? 1.0 : -1.0);
			} else {
				throw new ParseException(
						"Unrecognized feature \"" + element + "\" in definition.", features);
			}
		}
		return arr;
	}

}
