/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * @author Samantha Fiona McCabe
 * Date: 10/11/2014
 */
public final class DiskFileHandler implements FileHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DiskFileHandler.class);

	private final String encoding;

	public DiskFileHandler(String encodingParam) {
		encoding = encodingParam;
	}
	
	@Nullable
	@Override
	public CharSequence read(@NotNull String path) {
		return IOUtil.readPath(path);
	}

	@Override
	public boolean writeString(@NotNull String path, @NotNull CharSequence data) {
		File file = new File(path);
		try (Writer writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(data.toString());
			return true;
		} catch (IOException e) {
			LOG.error("Failed to write to path {}", path, e);
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
