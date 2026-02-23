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

package org.didelphis.language.phonetic.model;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.phonetic.PhoneticTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureSpecificationTest extends PhoneticTestBase {

	private static FeatureSpecification specification;

	@BeforeEach
	private void load() {
		specification =  loader.getSpecification();
	}

	@Test
	void testImport() {
		FeatureSpecification other = new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				"AT_hybrid.mapping").getSpecification();
		assertEquals(specification, other);
	}

	@Test
	void testSize() {
		int size = specification.size();
		assertEquals(20, size);
	}

	@Test
	void testGetIndexSonorant() {
		int index = specification.getIndex("sonorant");
		assertEquals(1, index);
	}

	@Test
	void testGetIndexLong() {
		int index = specification.getIndex("long");
		assertEquals(18, index);
	}

	@Test
	void testGetIndexBadFeature() {
		int index = specification.getIndex("x");
		assertEquals(-1, index);
	}
}
