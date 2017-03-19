/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Samantha Fiona Morrigan McCabe Created: 7/4/2016
 */
public class FeatureSpecificationTest {
	private static final transient Logger LOGGER = LoggerFactory.getLogger(
			FeatureSpecificationTest.class);
	
	private static final FeatureSpecification MODEL = load();

	private static FeatureSpecification load() {
		String path = "AT_hybrid.spec";
		FileHandler handler = ClassPathFileHandler.INSTANCE;
		return FeatureModelLoader.loadDouble(path, handler);
	}

	@Test
	void testSize() {
		int size = MODEL.size();
		Assertions.assertEquals(20, size);
	}

	@Test
	void testGetIndexSonorant() {
		int index = MODEL.getIndex("sonorant");
		Assertions.assertEquals(1, index);
	}

	@Test
	void testGetIndexLong() {
		int index = MODEL.getIndex("long");
		Assertions.assertEquals(18, index);
	}

	@Test
	void testGetIndexBadFeature() {
		int index = MODEL.getIndex("x");
		Assertions.assertEquals(-1, index);
	}
}
