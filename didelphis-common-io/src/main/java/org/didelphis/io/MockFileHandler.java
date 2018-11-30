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

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * Class {@code MockFileHandler}
 * <p>
 * Simulates a crude file-system, mapping paths to data.
 */
@Data
public final class MockFileHandler implements FileHandler {

	private final Map<String, String> mockFileSystem;

	@NonNull
	@Override
	public String read(@NonNull String path) {
		return mockFileSystem.get(path);
	}

	@Override
	public void writeString(@NonNull String path, @NonNull String data) {
		mockFileSystem.put(path, data);
	}

	@Override
	public boolean validForRead(@NonNull String path) {
		return mockFileSystem.containsKey(path);
	}

	@Override
	public boolean validForWrite(@NonNull String path) {
		return true;
	}

}
