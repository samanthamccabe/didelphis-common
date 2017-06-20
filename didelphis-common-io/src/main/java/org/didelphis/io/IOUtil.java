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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by samantha on 1/21/17.
 */
public final class IOUtil {

	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);

	private IOUtil() {}

	@Nullable
	public static String readPath(@NotNull String path) {
		File file = new File(path);
		try (InputStream stream = new FileInputStream(file)) {
			return readStream(stream);
		} catch (IOException e) {
			LOG.error("Failed to read from path {}", path, e);
		}
		return null;
	}

	@Nullable
	@Contract("null -> null")
	public static String readStream(@NotNull InputStream stream) {
		try (Reader reader = new BufferedReader(new InputStreamReader(stream))) {
			return readString(reader);
		} catch (IOException e) {
			LOG.error("Failed to read from stream", e);
		}
		return null;
	}

	@NotNull
	@Contract("null -> fail")
	private static String readString(@NotNull Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder(0x1000);
		int r = reader.read();
		while (r >= 0) {
			sb.append((char) r);
			r = reader.read();
		}
		return sb.toString();
	}
}
