/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.language.phonetic.model.interfaces;

import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;

import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 7/31/2016
 * 
 * A feature featureModel which includes constraints feature co-occurances
 */
public interface FeatureModel<N extends Number>
		extends FeatureSpecification {

	/**
	 * Retrieve this model's value {@code Constraint}s
	 * @return a list of feature value constraints; should be immutable
	 */
	List<Constraint<N>> getConstraints();

	/**
	 * Parses a well-formed 
	 * @param string
	 * @return a parsed {@code FeatureArray} or {@code null} if parsing fails
	 */
	FeatureArray<N> parseFeatureString(String string);
}
