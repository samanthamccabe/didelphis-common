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
