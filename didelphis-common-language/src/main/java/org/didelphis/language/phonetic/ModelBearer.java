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

package org.didelphis.language.phonetic;

import lombok.NonNull;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.utilities.Templates;

/**
 * Interface {@code ModelBearer}
 *
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
			String message = Templates.create()
					.add("Mismatch between models")
					.data(this)
					.data(bearer)
					.build();
			throw new IllegalArgumentException(message);
		}
	}
}
