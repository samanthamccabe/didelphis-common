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

package org.didelphis.common.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 3/16/17.
 */
class MockFileHandlerTest {
	
	private static Map<String, CharSequence> map;
	private static FileHandler handler;
	
	@BeforeAll
	static void init() {
		map = new HashMap<>();
		map.put("file1", "payload1: xx");
		map.put("file2", "payload2: yy");
		map.put("file3", "payload3: zz");
		map.put("file4", new StringBuilder("payloadX: 00"));
		handler = new MockFileHandler(map);
	}
	
	@Test
	void testHashCode() {
		assertEquals(Objects.hash(map), handler.hashCode() );
	}

	@Test
	void testEquals() {
		assertEquals(handler, new MockFileHandler(map));
	}

	@Test
	void read() {
		assertEquals("payload1: xx", handler.read("file1"));
		assertEquals("payload2: yy", handler.read("file2"));
		assertEquals("payload3: zz", handler.read("file3"));
		assertEquals("payloadX: 00", handler.read("file4").toString());
	}

	@Test
	void writeString() {
		handler.writeString("newFile","new payload");
		assertEquals("new payload", map.get("newFile"));
	}

	@Test
	void testToString() {
		assertEquals(handler, handler);
		assertEquals(handler, new MockFileHandler(map));
		assertNotEquals(handler, new MockFileHandler(new HashMap<>()));
	}
}
