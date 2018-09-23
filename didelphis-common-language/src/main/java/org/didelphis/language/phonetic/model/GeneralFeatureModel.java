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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.Regex;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.utilities.Templates;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.Map;

import static java.text.Normalizer.normalize;

/**
 * Class {@code GeneralFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class GeneralFeatureModel<T> implements FeatureModel<T> {

	static String VALUE  = "(-?\\d|[A-Zα-ω]+)";
	static String NAME   = "(\\w+)";
	static String ASSIGN = "([=:><])";

	static Regex VALUE_PATTERN   = new Regex(VALUE + ASSIGN + NAME);
	static Regex BINARY_PATTERN  = new Regex("([+\\-−])" + NAME);
	static Regex FEATURE_PATTERN = new Regex("[,;]\\s*|\\s+");
	static Regex BRACKET_PATTERN = new Regex("\\[(.+?)]");

	FeatureSpecification specification;
	List<Constraint<T>> constraints;
	Map<String, FeatureArray<T>> aliases;
	FeatureType<T> featureType;

	/**
	 * @param featureType
	 * @param specification
	 * @param constraints
	 * @param aliases
	 */
	public GeneralFeatureModel(
			@NonNull FeatureType<T> featureType,
			@NonNull FeatureSpecification specification,
			@NonNull List<Constraint<T>> constraints,
			@NonNull Map<String, FeatureArray<T>> aliases
	) {
		this.featureType = featureType;
		this.specification = specification;
		this.constraints = constraints;
		this.aliases = aliases;
	}
	
	@NonNull
	@Override
	public List<Constraint<T>> getConstraints() {
		return constraints;
	}

	@NonNull
	@Override
	public FeatureArray<T> parseFeatureString(@NonNull String string) {
		String normal = normalize(string, Form.NFKC);
		String pattern = BRACKET_PATTERN.replace(normal, "$1");
		FeatureArray<T> arr = new SparseFeatureArray<>(this);
		Map<String, Integer> indices = specification.getFeatureIndices();
		for (String element : FEATURE_PATTERN.split(pattern)) {
			Match<String> valueMatcher = VALUE_PATTERN.match(element);
			Match<String> binaryMatcher = BINARY_PATTERN.match(element);
			if (aliases.containsKey(element)) {
				arr.alter(aliases.get(element));
			} else {
				if (valueMatcher.matches()) {
					String featureName = valueMatcher.group(3);
					String assignment = valueMatcher.group(2);
					String featureValue = valueMatcher.group(1);
					int value = retrieveIndex(featureName, string, indices);
					arr.set(value, featureType.parseValue(featureValue));
				} else if (binaryMatcher.matches()) {
					String featureName = binaryMatcher.group(2);
					String featureValue = binaryMatcher.group(1);
					int value = retrieveIndex(featureName, string, indices);
					arr.set(value, featureType.parseValue(featureValue));
				} else {
					String message = Templates.create()
							.add("Unrecognized feature '{}' in definition")
							.with(element)
							.data(string)
							.build();
					throw new ParseException(message);
				}
			}
		}
		return arr;
	}

	@NonNull
	@Override
	public FeatureType<T> getFeatureType() {
		return featureType;
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	private static int retrieveIndex(
			@NonNull String label,
			@NonNull String string,
			@NonNull Map<String, Integer> names
	) {
		if (names.containsKey(label)) {
			return names.get(label);
		}
		String message = Templates.create()
				.add("Invalid feature label {}")
				.with(label)
				.data(string)
				.build();
		throw new ParseException(message);
	}
}
