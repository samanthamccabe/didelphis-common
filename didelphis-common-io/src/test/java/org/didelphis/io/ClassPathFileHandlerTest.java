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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by samantha on 3/16/17.
 */
class ClassPathFileHandlerTest {

	@Test
	void read() throws IOException {
		String received = ClassPathFileHandler.INSTANCE.read("testFile.txt");
		String expected = "this is a test file for ClassPathFileHandlerTest\n";
		assertEquals(expected, received);
	}

	@Test
	void write() {
		assertThrows(UnsupportedOperationException.class,
				() -> ClassPathFileHandler.INSTANCE.writeString("",""));
	}
}
