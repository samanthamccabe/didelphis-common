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

import java.util.List;

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
	List<String> split(@NonNull String string);

	/**
	 * Splits a string into components using reserved symbols
	 * @param string string to be segmented
	 * @param special reserved characters to be treated as unitary
	 * @return a list of strings
	 */
	@NonNull
	List<String> split(@NonNull String string, @NonNull Iterable<String> special);
}
