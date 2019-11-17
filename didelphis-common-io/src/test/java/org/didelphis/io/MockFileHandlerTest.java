/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MockFileHandlerTest {

	private static Map<String, String> map;
	private static FileHandler handler;

	@BeforeAll
	static void init() {
		map = new HashMap<>();
		map.put("file1", "payload1: xx");
		map.put("file2", "payload2: yy");
		map.put("file3", "payload3: zz");
		map.put("file4", "payloadX: 00");
		handler = new MockFileHandler(map);
	}

	@Test
	void testHashCode() {
		assertEquals(handler.hashCode(), new MockFileHandler(map).hashCode());
	}

	@Test
	void testEquals() {
		assertEquals(handler, new MockFileHandler(map));
	}

	@Test
	void read() throws IOException {
		assertEquals("payload1: xx", handler.read("file1"));
		assertEquals("payload2: yy", handler.read("file2"));
		assertEquals("payload3: zz", handler.read("file3"));
		assertEquals("payloadX: 00", handler.read("file4"));
	}

	@Test
	void writeString() throws IOException {
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
