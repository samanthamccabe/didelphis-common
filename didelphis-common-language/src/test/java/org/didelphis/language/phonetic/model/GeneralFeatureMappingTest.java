/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.phonetic.model;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.UndefinedSegment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneralFeatureMappingTest extends PhoneticTestBase {

	private static FeatureMapping<Integer> mapping;

	@BeforeAll
	static void init() {
		mapping = loader.getFeatureMapping();
	}

	@Test
	void testLoad01() {
		assertFalse(mapping.getFeatureMap().isEmpty());
		assertFalse(mapping.getModifiers().isEmpty());
	}

	@Test
	void testLoad02() {
		String resourceName = "AT_hybrid.mapping";
		FeatureMapping<Integer> model = loadMapping(resourceName);
		Assertions.assertNotNull(model.getSpecification());
		assertTrue(model.getSpecification().size() > 0);
	}

	@Test
	void testLoad_AT_Hybrid() {
		String resourceName = "AT_hybrid.model";
		FeatureMapping<Integer> model = loadMapping(resourceName);
		assertFalse(model.getFeatureMap().isEmpty());
		assertFalse(model.getModifiers().isEmpty());
	}
	
	@Test
	void testContainsKey() {
		assertTrue(mapping.containsKey("p"));
		assertFalse(mapping.containsKey("@"));
		assertThrows(
				NullPointerException.class,
				() -> mapping.containsKey(null)
		);
	}
	
	@Test
	void testBestSymbol01()  {
		testBestSymbol("g");
		testBestSymbol("gʱ");
		testBestSymbol("gʲ");
		testBestSymbol("kʷʰ");
		testBestSymbol("kːʷʰ");
	}

	@Test
	void testParseSegment01() {
		Segment<Integer> s1 = mapping.parseSegment("ts");
		assertFalse(s1 instanceof UndefinedSegment);
		Segment<Integer> s2 = mapping.parseSegment("t͡s");
		assertFalse(s2 instanceof UndefinedSegment);
		Segment<Integer> s3 = mapping.parseSegment("t͜s");
		assertFalse(s3 instanceof UndefinedSegment);

		assertEquals(s1.getFeatures(), s2.getFeatures());
		assertEquals(s1.getFeatures(), s3.getFeatures());
	}

	private static void testBestSymbol(String string) {
		Segment<Integer> segment = mapping.parseSegment(string);
		FeatureArray<Integer> array = segment.getFeatures();
		String bestSymbol = mapping.findBestSymbol(array);
		assertEquals(string, bestSymbol);
	}

	private static FeatureMapping<Integer> loadMapping(String resourceName) {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				resourceName);
		return loader.getFeatureMapping();
	}
}
