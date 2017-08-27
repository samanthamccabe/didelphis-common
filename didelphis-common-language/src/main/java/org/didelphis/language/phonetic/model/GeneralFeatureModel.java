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

import lombok.ToString;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.Normalizer.normalize;
import static org.didelphis.utilities.PatternUtils.compile;

/**
 * Class {@code GeneralFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * @date 2017-06-09
 */
@ToString
public final class GeneralFeatureModel<T> implements FeatureModel<T> {

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
	public GeneralFeatureModel(
			@NotNull FeatureType<T> featureType,
			@NotNull FeatureSpecification specification,
			@NotNull List<Constraint<T>> constraints,
			@NotNull Map<String, FeatureArray<T>> aliases) {
		this.featureType = featureType;
		this.specification = specification;
		this.constraints = constraints;
		this.aliases = aliases;
	}

	@Override
	public int hashCode() {
		return Objects.hash(specification, constraints, aliases, featureType);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		GeneralFeatureModel<?> other = (GeneralFeatureModel<?>) obj;
		return Objects.equals(specification, other.specification) &&
				Objects.equals(featureType, other.featureType) &&
				Objects.equals(constraints, other.constraints) &&
				Objects.equals(aliases, other.aliases);
	}

	@NotNull
	@Override
	public List<Constraint<T>> getConstraints() {
		return constraints;
	}

	@NotNull
	@Override
	public FeatureArray<T> parseFeatureString(@NotNull String string) {
		String normal = normalize(string, Form.NFKC);
		String pattern = BRACKETS_PATTERN.matcher(normal).replaceAll("$1");
		FeatureArray<T> arr = new SparseFeatureArray<>(this);
		Map<String, Integer> indices = specification.getFeatureIndices();
		for (String element : FEATURE_PATTERN.split(pattern)) {
			Matcher valueMatcher = VALUE_PATTERN.matcher(element);
			Matcher binaryMatcher = BINARY_PATTERN.matcher(element);
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
					throw new ParseException("Unrecognized feature \"" + 
							element + "\" in definition.", string);
				}
			}
		}
		return arr;
	}

	@NotNull
	@Override
	public FeatureType<T> getFeatureType() {
		return featureType;
	}

	@NotNull
	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	private static int retrieveIndex(String label, String string, @NotNull Map<String, Integer> names) {
		if (names.containsKey(label)) {
			return names.get(label);
		}
		throw new IllegalArgumentException("Invalid feature label " + label + " in " + string);
	}
}
