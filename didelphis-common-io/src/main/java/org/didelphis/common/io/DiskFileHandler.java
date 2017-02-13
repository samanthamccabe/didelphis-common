/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.didelphis.common.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 10/11/2014
 */
public class DiskFileHandler implements FileHandler {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(DiskFileHandler.class);

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final DiskFileHandler DEFAULT_INSTANCE = new DiskFileHandler(DEFAULT_ENCODING);

	private final String encoding;

	public DiskFileHandler(String encodingParam) {
		encoding = encodingParam;
	}

	public static DiskFileHandler getDefaultInstance() {
		return DEFAULT_INSTANCE;
	}

	@Override
	public CharSequence read(String path) {
		return IOUtil.readPath(path);
	}

	@Override
	public boolean writeString(String path, CharSequence data) {
		File file = new File(path);
		try (Writer writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(data.toString());
			return true;
		} catch (IOException e) {
			LOGGER.error("Failed to write to path {}", path, e);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(encoding);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (!(o instanceof DiskFileHandler)) { return false; }
		DiskFileHandler that = (DiskFileHandler) o;
		return Objects.equals(encoding, that.encoding);
	}

	@Override
	public String toString() {
		return "DiskFileHandler:" + encoding;
	}

}
