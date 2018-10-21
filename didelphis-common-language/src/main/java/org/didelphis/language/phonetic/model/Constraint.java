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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;

/**
 * Class {@code Constraint}
 * 
 * @since 0.1.0
 */
@EqualsAndHashCode(exclude = "featureModel")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Constraint<T> implements ModelBearer<T> {

	@Getter FeatureModel<T> featureModel;
	@Getter FeatureArray<T> source;
	@Getter FeatureArray<T> target;

	public Constraint(FeatureArray<T> source, FeatureArray<T> target) {
		source.consistencyCheck(target);
		featureModel = source.getFeatureModel();
		this.source = source;
		this.target = target;
	}

	public Constraint(@NonNull Constraint<T> constraint) {
		source = constraint.source;
		target = constraint.target;
		featureModel = constraint.featureModel;
	}
	
	@Override
	public String toString() {
		return source + " -> " + target;
	}
}
