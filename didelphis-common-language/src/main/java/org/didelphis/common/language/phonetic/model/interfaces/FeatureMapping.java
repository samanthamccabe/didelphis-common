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

import org.didelphis.common.language.phonetic.ModelBearer;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.segments.Segment;

import java.util.Map;
import java.util.Set;

/**
 * Provides a mapping between symbols and feature values in the context o a
 * feature specification
 */
public interface FeatureMapping<N> extends ModelBearer<N> {

	/**
	 * Computes a canonical {@code String} representation from the provided
	 * features. Output should be deterministic and consistent with the
	 * implementation of {@link #getSegment}
	 * @param featureArray the {@code FeatureArray} to decode
	 * @return //TODO:
	 */
	String findBestSymbol(FeatureArray<N> featureArray);

	/**
	 * 
	 * @return
	 */
	Set<String> getSymbols();

	/**
	 * 
	 * @param key
	 * @return
	 */
	boolean containsKey(String key);

	/**
	 * Provides a contained maps from symbols to features for base symbols
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	Map<String, FeatureArray<N>> getFeatureMap();

	/**
	 * Provides a contained maps from symbols to features for modifier and 
	 * diacritic characters
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	Map<String, FeatureArray<N>> getModifiers();

	/**
	 * Looks up the {@code FeatureArray} stored in the mapping under the
	 * provided string.
	 * @param key the symbol to look up in the mapping
	 * @return an associated {@code FeatureArray}; may be null if not found
	 */
	FeatureArray<N> getFeatureArray(String key);

	/**
	 * Recodes a string as a {@code Segment}; output must be consistent with
	 * the output of {@link #findBestSymbol}
	 * @param string a well formed {@code String} within the context of the
	 *      mapping. Cannot be {@code null}. 
	 * @return a new {@code Segment} decoded from the provided {@code string}.
	 *      Should not return {@code null}
	 */
	Segment<N> getSegment(String string);
}
