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

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;

import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/1/2016
 */
public class Constraint<T> implements ModelBearer<T> {

	private static final Pattern COMPILE = Pattern.compile("\\s+");
	
	private final String label;
	
	private final FeatureModel<T> featureModel;
	private final FeatureArray<T> source;
	private final FeatureArray<T> target;

	/**
	 * @param label
	 * @param source
	 * @param target
	 * @param featureModel
	 */
	public Constraint(CharSequence label,
	                  FeatureArray<T> source,
	                  FeatureArray<T> target,
	                  FeatureModel<T> featureModel) {

		this.label = COMPILE.matcher(label).replaceAll(" ");
		this.source = source;
		this.target = target;
		this.featureModel = featureModel;
	}

	public FeatureArray<T> getTarget() {
		return target;
	}

	public FeatureArray<T> getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "Constraint: " + label;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof Constraint)) { return false; }

		Constraint<?> constraint = (Constraint<?>) obj;
		return source.equals(constraint.source) &&
		       target.equals(constraint.source);
	}

	@Override
	public int hashCode() {
		return 31 * source.hashCode() * target.hashCode();
	}

	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}
}
