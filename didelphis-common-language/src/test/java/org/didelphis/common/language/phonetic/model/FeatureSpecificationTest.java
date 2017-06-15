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

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.language.phonetic.features.IntegerFeature;
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
	private static final Logger LOG = LoggerFactory.getLogger(
			FeatureSpecificationTest.class);
	
	private static final FeatureSpecification MODEL = load();

	private static FeatureSpecification load() {
		String path = "AT_hybrid.spec";
		return new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				path).getSpecification();
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
