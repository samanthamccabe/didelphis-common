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
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

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
@Slf4j
@UtilityClass
public final class IOUtil {

	public @Nullable String readPath(@NonNull String path) {
		File file = new File(path);
		try (InputStream stream = new FileInputStream(file)) {
			return readStream(stream);
		} catch (IOException e) {
			log.error("Failed to read from path {}", path, e);
		}
		return null;
	}

	public @Nullable String readStream(@NonNull InputStream stream) {
		try (Reader reader = new BufferedReader(new InputStreamReader(stream))) {
			return readString(reader);
		} catch (IOException e) {
			log.error("Failed to read from stream", e);
		}
		return null;
	}

	@NonNull
	@Contract("null -> fail")
	private String readString(@NonNull Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder(0x1000);
		int r = reader.read();
		while (r >= 0) {
			sb.append((char) r);
			r = reader.read();
		}
		return sb.toString();
	}
}
