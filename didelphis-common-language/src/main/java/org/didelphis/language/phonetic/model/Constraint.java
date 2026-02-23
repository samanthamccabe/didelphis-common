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
public class Constraint implements ModelBearer {

	@Getter FeatureModel featureModel;
	@Getter FeatureArray source;
	@Getter FeatureArray target;

	public Constraint(FeatureArray source, FeatureArray target) {
		source.consistencyCheck(target);
		featureModel = source.getFeatureModel();
		this.source = source;
		this.target = target;
	}

	public Constraint(@NonNull Constraint constraint) {
		source = constraint.source;
		target = constraint.target;
		featureModel = constraint.featureModel;
	}

	@Override
	public String toString() {
		return source + " -> " + target;
	}
}
