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

package org.didelphis.io;

import lombok.NonNull;
import org.didelphis.utilities.Exceptions;
import org.jetbrains.annotations.Nullable;

/**
 * @author Samantha Fiona McCabe
 * @date 10/11/2014
 *
 * 		Modular file IO facade. Designed to provide read-write capabilities in a
 * 		general and modular fashion.
 */
public interface FileHandler {

	/**
	 * Reads data from a provided path, if supported
	 *
	 * @param path where to read data from
	 *
	 * @return the data at the provided path; should be null on error
	 */
	@Nullable CharSequence read(@NonNull String path);

	/**
	 * @param path
	 * @param type
	 * @param <X>
	 *
	 * @return
	 *
	 * @throws X
	 */
	@NonNull
	default <X extends RuntimeException> CharSequence readOrThrow(
			@NonNull String path, @NonNull Class<X> type
	) {
		CharSequence result = read(path);
		if (result == null) throw Exceptions.create(type).build();
		return result;
	}

	/**
	 * Write data to a provided path, if supported
	 *
	 * @param path where to write the data
	 * @param data data to write
	 *
	 * @return true if write is successful; false if an exception is thrown
	 */
	boolean writeString(@NonNull String path, @NonNull CharSequence data);

}
