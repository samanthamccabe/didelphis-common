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

import java.io.InputStream;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 10/11/2014
 */
public enum ClassPathFileHandler implements FileHandler {
	INSTANCE;
	
	private final String encoding;

	ClassPathFileHandler() {
		encoding = "UTF-8";
	}

	@Override
	public String read(String path) {
		ClassLoader classLoader = ClassPathFileHandler.class.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream(path);
		if (stream == null) {
			throw new IllegalArgumentException("Unable to find classpath resource " + path);
		}
		return IOUtil.readStream(stream);
	}

	@Override
	public boolean writeString(String path, CharSequence data) {
		throw new UnsupportedOperationException(
				"Trying to write using an instance of "
						+ ClassPathFileHandler.class.getCanonicalName()
		);
	}

	@Override
	public String toString() {
		return "ClassPathFileHandler:" + encoding;
	}
}
