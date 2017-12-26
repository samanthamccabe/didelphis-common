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
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * @author Samantha Fiona McCabe
 * @date 10/11/2014
 */
@Slf4j
@ToString
@EqualsAndHashCode
public final class DiskFileHandler implements FileHandler {

	private final String encoding;

	public DiskFileHandler(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public @Nullable CharSequence read( @NonNull String path) {
		return IOUtil.readPath(path);
	}

	@Override
	public boolean writeString(
			 @NonNull String path,  @NonNull CharSequence data
	) {
		File file = new File(path);
		try (Writer writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(data.toString());
			return true;
		} catch (IOException e) {
			log.error("Failed to write to path {}", path, e);
		}
		return false;
	}
}
