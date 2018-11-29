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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.utilities.Logger;

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
 * 10/11/2014
 */

@ToString
@EqualsAndHashCode
public final class DiskFileHandler implements FileHandler {

	private static final Logger LOG = Logger.create(DiskFileHandler.class);
	
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
}
