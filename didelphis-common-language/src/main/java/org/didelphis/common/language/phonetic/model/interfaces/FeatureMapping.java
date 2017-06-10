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

package org.didelphis.common.language.phonetic.model.interfaces;

import org.didelphis.common.language.phonetic.ModelBearer;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * The {@code FeatureMapping} provides a mapping between symbols and feature
 * arrays. It is an extension of {@link ModelBearer} rather than
 * {@link FeatureModel} in order to express the fact that multiple mappings can
 * derive from the same model due to different notational standards.
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2017-02-16
 */
public interface FeatureMapping<N> extends ModelBearer<N> {

	/**
	 * Computes a canonical {@code String} representation from the provided
	 * features. Output should be deterministic and consistent with the
	 * implementation of {@link #parseSegment}
	 * @param featureArray the {@code FeatureArray} to decode
	 * @return //TODO:
	 */
	@NotNull
	String findBestSymbol(@NotNull FeatureArray<N> featureArray);

	/**
	 * Returns all symbols defined in the mapping.
	 * @return all symbols defined in the mapping.
	 */
	@NotNull
	Set<String> getSymbols();

	/**
	 * 
	 * @param key
	 * @return
	 */
	boolean containsKey(@NotNull String key);

	/**
	 * Provides a contained maps from symbols to features for base symbols
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	@NotNull
	Map<String, FeatureArray<N>> getFeatureMap();

	/**
	 * Provides a contained maps from symbols to features for modifier and 
	 * diacritic characters
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	@NotNull
	Map<String, FeatureArray<N>> getModifiers();

	/**
	 * Looks up the {@code FeatureArray} stored in the mapping under the
	 * provided string.
	 * @param key the symbol to look up in the mapping
	 * @return an associated {@code FeatureArray}; may be null if not found
	 */
	@Nullable
	FeatureArray<N> getFeatureArray(String key);

	/**
	 * Parses as string into a {@link Segment}
	 *
	 * If the output of this method is passed {@link #findBestSymbol}, the
	 * output of that method should be equal to the input of this one. That is,
	 *
	 * {@code findBestSymbol(parseSegment(string)) == string}
	 *
	 *
	 * @param string a well formed {@link String} whose constituent characters
	 *      are present in this mapping. Cannot be {@code null}.
	 * @return a new {@link Segment} parsed from the provided {@link String}.
	 * @throws org.didelphis.common.language.exceptions.ParseException if the
	 *      provided string is ill-formed or contains character not present in
	 *      the mapping
	 */
	@NotNull
	Segment<N> parseSegment(@NotNull String string);
}
