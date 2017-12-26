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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Class IOUtilTest
 *
 * @since 06/05/2017
 */
class IOUtilTest {

	@Test
	void readPath() throws IOException {
		String path = "pom.xml";
		String data = IOUtil.readPath(path);
		assertNotNull(data,"Failed to read "+new File(path).getCanonicalPath());
		assertFalse(data.isEmpty());
	}

	@Test
	void readPath_Fail() {
		String data = IOUtil.readPath("willfail");
		assertNull(data);
	}

	@Test
	void readStream() {
		String name = "testFile.txt";
		InputStream resource = IOUtilTest.class.getClassLoader().getResourceAsStream(name);
		String data = IOUtil.readStream(resource);
		assertNotNull(data);
		assertFalse(data.isEmpty());
	}
}
