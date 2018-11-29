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

import java.io.IOException;
import java.io.Reader;

/**
 * Interface {@code FileHandler}
 * <p>
 * Modular file IO facade. Designed to provide read-write capabilities in a
 * general and modular fashion.
 */
public interface FileHandler {

	/**
	 * Reads data from a provided path, if supported
	 *
	 * @param path where to read data from
	 * @return the data at the provided path
	 * @throws IOException if the path is invalid or cannot be read from
	 */
	@NonNull
	String read(@NonNull String path) throws IOException;

	/**
	 * Write data to a provided path, if supported
	 *
	 * @param path where to write the data
	 * @param data data to write
	 * @throws IOException if the path is invalid or cannot be written to
	 */
	void writeString(@NonNull String path, @NonNull String data)
			throws IOException;

	@NonNull
	static String readString(@NonNull Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder(0x1000);
		int r = reader.read();
		while (r >= 0) {
			sb.append((char) r);
			r = reader.read();
		}
		return sb.toString();
	}
}
