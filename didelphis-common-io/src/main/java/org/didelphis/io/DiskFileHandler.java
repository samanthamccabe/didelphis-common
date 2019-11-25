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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;


/**
 * Class {@code DiskFileHandler}
 *
 *
 */
@ToString
@EqualsAndHashCode
public final class DiskFileHandler implements FileHandler {

	private final String encoding;

	public DiskFileHandler(String encoding) {
		this.encoding = encoding;
	}

	@NonNull
	@Override
	public String read(@NonNull String path) throws IOException {
		File file = new File(path);
		try (
				InputStream stream = new FileInputStream(file);
				Reader reader = new InputStreamReader(stream)
		) {
			return FileHandler.readString(reader);
		}
	}

	@Override
	public void writeString(@NonNull String path, @NonNull String data)
			throws IOException {
		File file = new File(path);
		try (Writer writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(data);
		}
	}

	@Override
	public boolean validForRead(@NonNull String path) {
		File file = new File(path);
		return file.exists() && file.canRead();
	}

	@Override
	public boolean validForWrite(@NonNull String path) {
		File file = new File(path);
		return file.exists() && file.canWrite();
	}
}
