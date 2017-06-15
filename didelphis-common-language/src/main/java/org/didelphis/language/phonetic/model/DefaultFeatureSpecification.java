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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/16/17.
 * 
 * Reference implementation of the {@code FeatureSpecification} interface
 */
public final class DefaultFeatureSpecification implements FeatureSpecification {
	
	public static final FeatureSpecification EMPTY = new DefaultFeatureSpecification();
	
	private final int size;

	private final List<String> featureNames;
	private final Map<String, Integer> featureIndices;

	private DefaultFeatureSpecification() {
		this(new ArrayList<>(), new HashMap<>());
	}

	/**
	 * @param names
	 * @param types
	 * @param indices
	 */
	public DefaultFeatureSpecification(List<String> names, Map<String, Integer> indices) {
		size = names.size();
		featureNames = names;
		featureIndices = indices;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public Map<String, Integer> getFeatureIndices() {
		return Collections.unmodifiableMap(featureIndices);
	}

	@Override
	public int getIndex(@NotNull String featureName) {
		Integer index = featureIndices.get(featureName);
		return index == null ? -1 : index;
	}

	@Override
	public List<String> getFeatureNames() {
		return Collections.unmodifiableList(featureNames);
	}

	@Override
	public int hashCode() {
		int code = size;
		code *= 31 + featureIndices.hashCode();
		code *= 31 + featureNames.hashCode();
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DefaultFeatureSpecification) {
			DefaultFeatureSpecification that = (DefaultFeatureSpecification) obj;
			return size == that.size 
			       && featureIndices.equals(that.featureIndices) 
			       && featureNames.equals(that.featureNames);
		}
		return false;
	}

	@Override
	public String toString() {
		return "DefaultFeatureSpecification"
		       + "{ size=" + size
		       + ", featureNames=" + featureNames
		       + ", featureIndices=" + featureIndices
		       + " }";
	}
}
