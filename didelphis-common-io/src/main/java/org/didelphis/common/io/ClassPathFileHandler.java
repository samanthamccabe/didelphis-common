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

import java.io.InputStream;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 10/11/2014
 */
public class ClassPathFileHandler implements FileHandler {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(ClassPathFileHandler.class);

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final ClassPathFileHandler DEFAULT_INSTANCE = new ClassPathFileHandler(DEFAULT_ENCODING);

	private final String encoding;

	private ClassPathFileHandler(String encodingParam) {
		encoding = encodingParam;
	}

	public static ClassPathFileHandler getDefault() {
		return DEFAULT_INSTANCE;
	}

	@Override
	public String read(String path) {
		ClassLoader classLoader = ClassPathFileHandler.class.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream(path);
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
