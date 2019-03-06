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

package org.didelphis.language.parsing;

import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 1/22/17.
 */
public interface Segmenter {

	/**
	 * Splits a string into components using reserved symbols
	 * @param string string to be segmented
	 * @return a list of strings
	 */
	@NonNull
	default List<String> split(@NonNull String string) {
		return split(string, Collections.emptyList(), Collections.emptyMap());
	}

	/**
	 * Splits a string into components using reserved symbols
	 *
	 * @param string string to be segmented
	 * @param special reserved characters to be treated as unitary
	 * @param delimiters a map of opening and closing delimiters which will
	 * 		not be split; that is, everything between the matching start and end
	 * 		delimiters will be preserved intact
	 *
	 * @return a list of strings; not {@code null}
	 */
	@NonNull
	List<String> split(
			@NonNull String string,
			@NonNull Iterable<String> special,
			@NonNull Map<String, String> delimiters
	);
}
