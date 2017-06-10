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

package org.didelphis.common.language.enums;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/15/17.
 */
@Disabled // TODO: 
class FormatterModeTest {
	@Test
	void normalizeNone() {
		String string = "string";
		assertEquals(string, FormatterMode.NONE.normalize(string));
	}

	@Test
	void splitNone() {
		String string = "string";
		assertTrue(false);
	}

	@Test
	void normalizeDecomposition() {
		assertTrue(false);
	}

	@Test
	void splitDecomposition() {
		assertTrue(false);
	}

	@Test
	void normalizeComposition() {
		assertTrue(false);
	}

	@Test
	void splitComposition() {
		assertTrue(false);
	}

	@Test
	void normalizeIntelligent() {
		assertTrue(false);
	}

	@Test
	void splitIntelligent() {
		assertTrue(false);
	}
	
	@Test
	void valueOf() {
	}

}
