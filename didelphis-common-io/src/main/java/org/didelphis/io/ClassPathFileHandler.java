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
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

/**
 * Enum {@code ClassPathFileHandler}
 *
 * Primarily
 *
 * @author Samantha Fiona McCabe
 * @date 10/11/2014
 * @since 0.1.0
 */
@ToString
public enum ClassPathFileHandler implements FileHandler {
	INSTANCE;

	private final String encoding;

	ClassPathFileHandler() {
		encoding = "UTF-8";
	}

	@Override
	public @Nullable String read(@NonNull String path) {
		ClassLoader classLoader = ClassPathFileHandler.class.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream(path);
		if (stream == null) return null; // TODO:
		return IOUtil.readStream(stream);
	}

	@Override
	public boolean writeString(
			 @NonNull String path,  @NonNull String data
	) {
		throw new UnsupportedOperationException(
				"Trying to write using an instance of "
						+ ClassPathFileHandler.class.getCanonicalName());
	}
}
