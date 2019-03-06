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

class BinaryFeatureTest {


	private static final BinaryFeature FEATURE = BinaryFeature.INSTANCE;
	private static final Boolean FALSE = Boolean.FALSE;
	private static final Boolean TRUE = Boolean.TRUE;
	private static final Class<NumberFormatException> NUMBER_FORMAT_EXCEPTION 
			= NumberFormatException.class;

	@Test
	void testEmptyLoader() {
		FeatureModelLoader<Boolean> loader = BinaryFeature.INSTANCE.emptyLoader();
		assertEquals(0, loader.getSpecification().size());
	}

	@Test
	void testParseValue() {
		assertEquals(TRUE,  FEATURE.parseValue("+"));
		assertEquals(TRUE,  FEATURE.parseValue("1"));
		assertEquals(FALSE, FEATURE.parseValue("-"));
		assertEquals(FALSE, FEATURE.parseValue(""));
		assertEquals(FALSE, FEATURE.parseValue("0"));
		
		assertThrows(NUMBER_FORMAT_EXCEPTION, () -> FEATURE.parseValue("/"));
		assertThrows(NUMBER_FORMAT_EXCEPTION, () -> FEATURE.parseValue("-1"));
		assertThrows(NUMBER_FORMAT_EXCEPTION, () -> FEATURE.parseValue("A"));
	}

	@Test
	void listUndefined() {
		Collection<Boolean> actual = FEATURE.listUndefined();
		assertTrue(actual.contains(null));
	}

	@Test
	void compare() {
		assertEquals(-1, FEATURE.compare(FALSE, TRUE));
		assertEquals( 1, FEATURE.compare(TRUE,  FALSE));
		assertEquals( 0, FEATURE.compare(TRUE,  TRUE));
		assertEquals( 0, FEATURE.compare(FALSE, FALSE));

		assertEquals(-1, FEATURE.compare(null,  TRUE));
		assertEquals( 1, FEATURE.compare(TRUE,  null));
		assertEquals( 0, FEATURE.compare(null,  FALSE));
		assertEquals( 0, FEATURE.compare(FALSE, null));
		assertEquals( 0, FEATURE.compare(null,  null));
	}

	@Test
	void difference() {
		assertEquals(1.0, FEATURE.difference(FALSE, TRUE));
		assertEquals(1.0, FEATURE.difference(TRUE,  FALSE));
		assertEquals(0.0, FEATURE.difference(TRUE,  TRUE));
		assertEquals(0.0, FEATURE.difference(FALSE, FALSE));
		assertEquals(1.0, FEATURE.difference(null,  TRUE));
		assertEquals(1.0, FEATURE.difference(TRUE,  null));
		assertEquals(0.0, FEATURE.difference(null,  FALSE));
		assertEquals(0.0, FEATURE.difference(FALSE, null));
		assertEquals(0.0, FEATURE.difference(null,  (Boolean) null));
	}

	@Test
	void intValue() {
		assertEquals(0, FEATURE.intValue(null));
		assertEquals(0, FEATURE.intValue(FALSE));
		assertEquals(1, FEATURE.intValue(TRUE));
	}

	@Test
	void doubleValue() {
		assertEquals(0.0, FEATURE.doubleValue(null));
		assertEquals(0.0, FEATURE.doubleValue(FALSE));
		assertEquals(1.0, FEATURE.doubleValue(TRUE));
	}
}