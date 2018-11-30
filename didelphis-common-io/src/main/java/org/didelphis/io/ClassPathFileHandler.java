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
import org.didelphis.utilities.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Enum {@code ClassPathFileHandler}
 * <p>
 * Read-only {@code FileHandler}
 */
@ToString
public enum ClassPathFileHandler implements FileHandler {INSTANCE;

	private static final Class<?> CLASS = ClassPathFileHandler.class;
	private static final Logger LOG = Logger.create(CLASS);
	private static final ClassLoader LOADER = CLASS.getClassLoader();

	private final String encoding;

	ClassPathFileHandler() {
		encoding = "UTF-8";
	}

	@NonNull
	@SuppressWarnings ("ProhibitedExceptionCaught")
	@Override
	public String read(@NonNull String path) throws IOException {
		try (InputStream stream = LOADER.getResourceAsStream(path);
				Reader reader = new InputStreamReader(stream, encoding)) {
			return FileHandler.readString(reader);
		} catch (NullPointerException e) {
			throw new IOException("Data not found on classpath at " + path, e);
		}
	}

	@Override
	public void writeString(
			@NonNull String path, @NonNull String data
	) {
		throw new UnsupportedOperationException(
				"Trying to write using an instance of " +
						CLASS.getCanonicalName());
	}

	@Override
	public boolean validForRead(@NonNull String path) {
		try (InputStream stream = LOADER.getResourceAsStream(path)) {
			return stream != null;
		} catch (IOException e) {
			LOG.warn("Failed to read from path {}", path, e);
		}
		return false;
	}

	@Override
	public boolean validForWrite(@NonNull String path) {
		return false;
	}}
