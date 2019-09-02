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
