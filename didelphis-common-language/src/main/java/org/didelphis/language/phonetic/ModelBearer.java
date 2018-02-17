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

package org.didelphis.language.phonetic;

import lombok.NonNull;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.utilities.Exceptions;

/**
 * Interface {@code ModelBearer}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-19
 * @since 0.1.0
 */
@FunctionalInterface
public interface ModelBearer<T> extends SpecificationBearer {

	@NonNull
	FeatureModel<T> getFeatureModel();

	@NonNull
	@Override
	default FeatureSpecification getSpecification() {
		return getFeatureModel().getSpecification();
	}

	default void consistencyCheck(@NonNull ModelBearer<T> bearer) {
		if (!getFeatureModel().equals(bearer.getFeatureModel())) {
			throw Exceptions.illegalArgument()
					.add("Mismatch between models")
					.data(this)
					.data(bearer)
					.build();
		}
	}
}
