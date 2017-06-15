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

import org.didelphis.language.exceptions.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.Normalizer.normalize;
import static org.didelphis.utilities.Patterns.compile;

/**
 * Class {@code GeneralFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2017-06-09
 */
public class GeneralFeatureModel<T> implements FeatureModel<T> {

	private static final String VALUE = "(-?\\d|[A-Zα-ω]+)";
	private static final String NAME = "(\\w+)";
	private static final String ASSIGN = "([=:><])";

	private static final Pattern VALUE_PATTERN = compile(VALUE, ASSIGN, NAME);
	private static final Pattern BINARY_PATTERN = compile("([+\\-−])", NAME);
	private static final Pattern FEATURE_PATTERN = compile("[,;]\\s*|\\s+");
	private static final Pattern BRACKETS_PATTERN = compile("\\[([^]]+)]");

	private final FeatureSpecification specification;
	private final List<Constraint<T>> constraints;
	private final Map<String, FeatureArray<T>> aliases;
	private final FeatureType<T> featureType;

	/**
	 * @param featureType
	 * @param specification
	 * @param constraints
	 * @param aliases
	 */
	public GeneralFeatureModel(FeatureType<T> featureType,
			FeatureSpecification specification,
			List<Constraint<T>> constraints,
			Map<String, FeatureArray<T>> aliases) {
		this.featureType = featureType;
		this.specification = specification;
		this.constraints = constraints;
		this.aliases = aliases;
	}

	@NotNull
	@Override
	public FeatureType<T> getFeatureType() {
		return featureType;
	}

	@NotNull
	@Override
	public FeatureArray<T> parseFeatureString(@NotNull String string) {
		String pattern = BRACKETS_PATTERN.matcher(normalize(string, Form.NFKC))
				.replaceAll("$1");
		FeatureArray<T> arr = new SparseFeatureArray<>(this);
		for (String element : FEATURE_PATTERN.split(pattern)) {
			Matcher valueMatcher = VALUE_PATTERN.matcher(element);
			Matcher binaryMatcher = BINARY_PATTERN.matcher(element);

			if (aliases.containsKey(element)) {
				arr.alter(aliases.get(element));
			} else if (valueMatcher.matches()) {
				String featureName = valueMatcher.group(3);
				String assignment = valueMatcher.group(2);
				String featureValue = valueMatcher.group(1);
				Integer value = retrieveIndex(featureName, string, specification.getFeatureIndices());
				arr.set(value, featureType.parseValue(featureValue));
			} else if (binaryMatcher.matches()) {
				String featureName = binaryMatcher.group(2);
				String featureValue = binaryMatcher.group(1);
				Integer value = retrieveIndex(featureName, string, getFeatureIndices());
				arr.set(value, featureType.parseValue(featureValue));
			} else {
				throw new ParseException("Unrecognized feature \"" + element +
						"\" in definition.", string);
			}
		}
		return arr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(specification, constraints, aliases, featureType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final GeneralFeatureModel other = (GeneralFeatureModel) obj;
		return Objects.equals(this.specification, other.specification) &&
				Objects.equals(this.constraints, other.constraints) &&
				Objects.equals(this.aliases, other.aliases) &&
				Objects.equals(this.featureType, other.featureType);
	}

	private static Integer retrieveIndex(String label, String features,
			Map<String, Integer> names) {
		if (names.containsKey(label)) {
			return names.get(label);
		}
		throw new ParseException("Invalid feature label", features);
	}

	@NotNull
	@Override
	public List<Constraint<T>> getConstraints() {
		return constraints;
	}

	@Override
	public int size() {
		return specification.size();
	}

	@NotNull
	@Override
	public Map<String, Integer> getFeatureIndices() {
		return specification.getFeatureIndices();
	}

	@Override
	public int getIndex(@NotNull String featureName) {
		return specification.getIndex(featureName);
	}

	@NotNull
	@Override
	public List<String> getFeatureNames() {
		return specification.getFeatureNames();
	}
}
