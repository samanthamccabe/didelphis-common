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
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SpecificationBearer;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;

import java.util.List;

/**
 * Interface {@code FeatureModel} is a {@link FeatureSpecification} which
 * has value constraints and a type parameter. It also provides a specification
 * for what feature values are  "defined" or "undefined" and defines a natural
 * ordering of elements.
 *
 * @since 0.1.0
 *
 */
public interface FeatureModel<T> extends SpecificationBearer {

	/**
	 * Retrieve this model's value {@link Constraint}s
	 * @return a list of feature value constraints; should be immutable
	 */
	@NonNull
	List<Constraint<T>> getConstraints();

	/**
	 * Parses a well-formed feature {@link String} into the corresponding array
	 * @param string the bracketed feature string definition to parse
	 * @return a parsed {@link FeatureArray}
	 *
	 * @throws ParseException if the
	 *
	 */
	@NonNull
	FeatureArray<T> parseFeatureString(@NonNull String string);

	/**
	 *
	 * @return
	 */
	@NonNull
	FeatureType<T> getFeatureType();
}
