/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.phonetic.model;

import lombok.NonNull;
import lombok.ToString;

import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.utilities.Templates;

import java.text.Normalizer.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.text.Normalizer.*;
import static org.didelphis.language.phonetic.model.ModelConstants.*;

/**
 * Class {@code GeneralFeatureModel}
 *
 * @since 0.1.0
 */
@ToString(exclude = "hash")
public final class GeneralFeatureModel<T> implements FeatureModel<T> {

	private int hash;

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
			@NonNull FeatureType<T> featureType,
			@NonNull FeatureSpecification specification,
			@NonNull List<Constraint<T>> constraints,
			@NonNull Map<String, FeatureArray<T>> aliases
	) {
		this.featureType = featureType;
		this.specification = specification;
		this.constraints = Collections.unmodifiableList(constraints);
		this.aliases = Collections.unmodifiableMap(aliases);
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
							.add("Unrecognized feature {} in definition")
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeneralFeatureModel<?> that = (GeneralFeatureModel<?>) o;
		return specification.equals(that.specification) &&
				constraints.equals(that.constraints) &&
				aliases.equals(that.aliases) &&
				featureType.equals(that.featureType);
	}

	@SuppressWarnings ("NonFinalFieldReferencedInHashCode")
	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = Objects.hash(specification, constraints, aliases, featureType);
		}
		return hash;
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
