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

package org.didelphis.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplatesTest {

	@Test
	void create() {
		String message = Templates.create()
				.add("Too few features are provided!")
				.add("Found {} but was expecting {}")
				.with(2, 3)
				.data("w\t+\t+")
				.build();
		
		String expected = "Too few features are provided! " +
				"Found 2 but was expecting 3\nWith Data:\n\tw\t+\t+" +
				"\n\n";
		
		assertEquals(expected, message);
	}

	@Test
	void compile() {
		String expected = "Found 2 but was expecting 3";
		String template = "Found {} but was expecting {}";
		String actual = Templates.compile(template, 2, 3);
		assertEquals(expected, actual);
	}
}