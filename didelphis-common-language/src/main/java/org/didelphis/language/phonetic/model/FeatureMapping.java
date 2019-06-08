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
import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * The {@code FeatureMapping} provides a mapping between symbols and feature
 * arrays. It is an extension of {@link ModelBearer} rather than
 * {@link FeatureModel} in order to express the fact that multiple mappings can
 * derive from the same model due to different notational standards.
 *
 * @since 0.1.0
 *
 */
public interface FeatureMapping<T> extends ModelBearer<T> {

	/**
	 * Computes a canonical {@code String} representation from the provided
	 * features. Output should be deterministic and consistent with the
	 * implementation of {@link #parseSegment}
	 * @param featureArray the {@code FeatureArray} to decode
	 * @return //TODO:
	 */
	@NonNull
	String findBestSymbol(@NonNull FeatureArray<T> featureArray);

	/**
	 * Returns all symbols defined in the mapping.
	 * @return all symbols defined in the mapping.
	 */
	@NonNull
	Set<String> getSymbols();

	/**
	 * 
	 * @param key
	 * @return
	 */
	boolean containsKey(@NonNull String key);

	/**
	 * Provides a contained maps from symbols to features for base symbols
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	@NonNull
	Map<String, FeatureArray<T>> getFeatureMap();

	/**
	 * Provides a contained maps from symbols to features for modifier and 
	 * diacritic characters
	 * @return a maps containing the relevant data; it is recommended that
	 *      this not be modifiable
	 */
	@NonNull
	Map<String, FeatureArray<T>> getModifiers();

	/**
	 * Looks up the {@code FeatureArray} stored in the mapping under the
	 * provided string.
	 * @param key the symbol to look up in the mapping
	 * @return an associated {@code FeatureArray}; may be null if not found
	 */
	@Nullable
	FeatureArray<T> getFeatureArray(@NonNull String key);

	/**
	 * Parses as string into a {@link Segment}
	 *
	 * If the output of this method is passed {@link #findBestSymbol}, the
	 * output of that method should be equal to the input of this one. That is,
	 *
	 * {@code findBestSymbol(parseSegment(string)) == string}
	 *
	 * @param string a well formed {@link String} whose constituent characters
	 *      are present in this mapping. Cannot be {@code null}.
	 * @return a new {@link Segment} parsed from the provided {@link String}. If
	 * the mapping is non-empty but does not contain a valid representation,
	 * then an {@link org.didelphis.language.phonetic.segments.UndefinedSegment}
	 * should be returned.
	 * @throws ParseException if the
	 *      provided string is ill-formed or contains character not present in
	 *      the mapping
	 */
	@NonNull
	Segment<T> parseSegment(@NonNull String string);
}
