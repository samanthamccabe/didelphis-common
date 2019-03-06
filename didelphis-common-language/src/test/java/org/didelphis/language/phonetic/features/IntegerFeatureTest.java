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

package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFeatureTest {

	private static final IntegerFeature FEATURE = IntegerFeature.INSTANCE;
	private static final double NaN = Double.NaN;

	@Test
	void testEmptyLoader() {
		FeatureModelLoader<Integer> loader = IntegerFeature.INSTANCE.emptyLoader();
		assertEquals(0, loader.getSpecification().size());
	}

	@Test
	void testParseValue() {
		assertEquals( 1, (int) FEATURE.parseValue("+"));
		assertEquals( 1, (int) FEATURE.parseValue("1"));
		assertEquals(-1, (int) FEATURE.parseValue("-"));
		assertEquals(-1, (int) FEATURE.parseValue("-1"));
		assertEquals( 0, (int) FEATURE.parseValue(""));
		assertEquals( 0, (int) FEATURE.parseValue("0"));

		assertThrows(NumberFormatException.class, () -> FEATURE.parseValue("/"));
	}

	@Test
	void listUndefined() {
		Collection<Integer> actual = FEATURE.listUndefined();
		assertTrue(actual.contains(null));
	}

	@Test
	void compare() {
		assertEquals(-1, FEATURE.compare(0, 1));
		assertEquals( 1, FEATURE.compare(1, 0));
		assertEquals( 0, FEATURE.compare(1, 1));

		assertEquals( 0, FEATURE.compare(null, 0));
		assertEquals( 0, FEATURE.compare(0, null));
		assertEquals( 0, FEATURE.compare(null, null));
	}

	@Test
	void difference() {
		assertEquals(0.0, FEATURE.difference(0, 0));
		assertEquals(0.0, FEATURE.difference(1, 1));
		assertEquals(1.0, FEATURE.difference(0, 1));
		assertEquals(1.0, FEATURE.difference(1, 0));
		assertEquals(0.0, FEATURE.difference(null, (Integer) null));
		assertEquals(1.0, FEATURE.difference(1, null));
		assertEquals(1.0, FEATURE.difference(null, 1));
		assertEquals(0.0, FEATURE.difference(null, 0));
	}

	@Test
	void intValue() {
		assertEquals(0, FEATURE.intValue(null));
		assertEquals(0, FEATURE.intValue(0));
		assertEquals(1, FEATURE.intValue(1));
	}

	@Test
	void doubleValue() {
		assertEquals(NaN, FEATURE.doubleValue(null));
		assertEquals(0.0, FEATURE.doubleValue(0));
		assertEquals(1.0, FEATURE.doubleValue(1));
	}
}