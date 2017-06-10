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

import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.Normalizer.normalize;
import static org.didelphis.common.utilities.Patterns.compile;

/**
 * Class {@code AbstractFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2017-06-09
 */
public abstract class AbstractFeatureModel<N> implements FeatureModel<N> {

	private static final String VALUE = "(-?\\d|[A-Zα-ω]+)";
	private static final String NAME = "(\\w+)";
	private static final String ASSIGN = "([=:><])";

	private static final Pattern VALUE_PATTERN = compile(VALUE, ASSIGN, NAME);
	private static final Pattern BINARY_PATTERN = compile("([+\\-−])", NAME);
	private static final Pattern FEATURE_PATTERN = compile("[,;]\\s*|\\s+");

	private final FeatureSpecification specification;
	private final List<Constraint<N>> constraints;
	private final Map<String, FeatureArray<N>> aliases;

	/**
	 * @param specification
	 * @param constraints
	 * @param aliases
	 */
	public AbstractFeatureModel(FeatureSpecification specification,
			List<Constraint<N>> constraints,
			Map<String, FeatureArray<N>> aliases) {
		this.specification = specification;
		this.constraints = constraints;
		this.aliases = aliases;
	}

	@NotNull
	@Override
	public FeatureArray<N> parseFeatureString(@NotNull String string) {
		String pattern = normalize(string, Form.NFKC);
		FeatureArray<N> arr = new SparseFeatureArray<>(this);
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
				arr.set(value, parseValue(featureValue));
			} else if (binaryMatcher.matches()) {
				String featureName = binaryMatcher.group(2);
				String featureValue = binaryMatcher.group(1);
				Integer value = retrieveIndex(featureName, string, getFeatureIndices());
				arr.set(value, parseValue(featureValue));
			} else {
				throw new ParseException("Unrecognized feature \"" + element +
						"\" in definition.", string);
			}
		}
		return arr;
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
	public List<Constraint<N>> getConstraints() {
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
