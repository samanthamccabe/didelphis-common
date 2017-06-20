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

import java.util.Map;
import java.util.Objects;

/**
 * @author Samantha Fiona McCabe
 * @since 10/13/2014
 * This mock handler simply uses maps to simulate a crude file-system
 * The maps is from 'path' to data, so a test can instantiate the class
 * with this object, either providing it data, or reading from it;
 */
public final class MockFileHandler implements FileHandler {

	private final Map<String, CharSequence> mockFileSystem;

	public MockFileHandler(Map<String, CharSequence> input) {
		mockFileSystem = input;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mockFileSystem);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MockFileHandler)) return false;
		MockFileHandler that = (MockFileHandler) o;
		return Objects.equals(mockFileSystem, that.mockFileSystem);
	}

	@Override
	public String toString() {
		return "MockFileHandler:" + mockFileSystem;
	}

	@Override
	public CharSequence read(String path) {
		return mockFileSystem.get(path);
	}

	@Override
	public boolean writeString(String path, CharSequence data) {
		mockFileSystem.put(path, data);
		return true;
	}

}
