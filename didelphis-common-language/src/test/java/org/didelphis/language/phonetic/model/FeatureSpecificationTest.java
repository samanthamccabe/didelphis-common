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

package org.didelphis.language.phonetic.model;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Samantha Fiona Morrigan McCabe Created: 7/4/2016
 */
public class FeatureSpecificationTest {
	private static final Logger LOG = LoggerFactory.getLogger(
			FeatureSpecificationTest.class);
	
	private static FeatureSpecification model;

	@BeforeAll
	private static void load() {
		String path = "AT_hybrid.spec";
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE, ClassPathFileHandler.INSTANCE, path);
		model =  loader.getSpecification();
	}

	@Test
	void testImport() {
		FeatureSpecification other = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE, ClassPathFileHandler.INSTANCE,
				"AT_hybrid.mapping").getSpecification();
		assertEquals(model, other);
	}

	@Test
	void testSize() {
		int size = model.size();
		assertEquals(20, size);
	}

	@Test
	void testGetIndexSonorant() {
		int index = model.getIndex("sonorant");
		assertEquals(1, index);
	}

	@Test
	void testGetIndexLong() {
		int index = model.getIndex("long");
		assertEquals(18, index);
	}

	@Test
	void testGetIndexBadFeature() {
		int index = model.getIndex("x");
		assertEquals(-1, index);
	}
}
