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
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samantha Fiona McCabe Created: 7/4/2016
 */
public class FeatureSpecificationTest extends PhoneticTestBase {
	private static final Logger LOG = LoggerFactory.getLogger(
			FeatureSpecificationTest.class);
	
	private static FeatureSpecification specification;

	@BeforeAll
	private static void load() {
		specification =  loader.getSpecification();
	}

	@Test
	void testImport() {
		FeatureSpecification other = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE, ClassPathFileHandler.INSTANCE,
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
