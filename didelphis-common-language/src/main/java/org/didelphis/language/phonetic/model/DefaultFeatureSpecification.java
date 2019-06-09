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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Reference implementation of the {@code FeatureSpecification} interface
 */
@EqualsAndHashCode
@ToString
public final class DefaultFeatureSpecification implements FeatureSpecification {

	private final int size;

	@Getter private final List<String> featureNames;
	@Getter private final Map<String, Integer> featureIndices;

	public DefaultFeatureSpecification() {
		this(new ArrayList<>(), new HashMap<>());
	}

	/**
	 * @param names
	 * @param indices
	 */
	public DefaultFeatureSpecification(
			@NonNull List<String> names, @NonNull Map<String, Integer> indices
	) {
		size = names.size();
		featureNames = Collections.unmodifiableList(names);
		featureIndices = Collections.unmodifiableMap(indices);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int getIndex(@NonNull String featureName) {
		Integer index = featureIndices.get(featureName);
		return index == null ? -1 : index;
	}

}
