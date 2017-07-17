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
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.segments.Segment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Samantha Fiona McCabe
 */
public class GeneralFeatureMappingTest extends PhoneticTestBase {

	private static FeatureMapping<Integer> mapping;

	@BeforeAll
	static void init() {
		mapping = loader.getFeatureMapping();
	}

	@Test
	void testLoad01() {
		Assertions.assertFalse(mapping.getFeatureMap().isEmpty());
		Assertions.assertFalse(mapping.getModifiers().isEmpty());
	}

	@Test
	void testLoad02() {
		String resourceName = "AT_hybrid.mapping";
		FeatureMapping<Integer> model = loadMapping(resourceName, FormatterMode.INTELLIGENT);
		Assertions.assertNotNull(model.getSpecification());
		Assertions.assertTrue(model.getSpecification().size() > 0);
	}

	@Test
	void testLoad_AT_Hybrid() {
		String resourceName = "AT_hybrid.model";
		FeatureMapping<Integer> model = loadMapping(resourceName, FormatterMode.INTELLIGENT);
		Assertions.assertFalse(model.getFeatureMap().isEmpty());
		Assertions.assertFalse(model.getModifiers().isEmpty());
	}
	
	@Test
	void testGetStringFromFeatures01()  {
		testBestSymbol("g");
	}

	@Test
	void testGetStringFromFeatures02()  {
		testBestSymbol("gʱ");
	}

	@Test
	void testGetStringFromFeatures03()  {
		testBestSymbol("gʲ");
	}

	@Test
	void testGetStringFromFeatures04()  {
		testBestSymbol("kʷʰ");
	}

	@Test
	void testGetStringFromFeatures05()  {
		testBestSymbol("kːʷʰ");
	}
	
	private static void testBestSymbol(String string) {
		Segment<Integer> segment = mapping.parseSegment(string);
		FeatureArray<Integer> array = segment.getFeatures();
		String bestSymbol = mapping.findBestSymbol(array);
		Assertions.assertEquals(string, bestSymbol);
	}

	private static FeatureMapping<Integer> loadMapping(String resourceName, FormatterMode mode) {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				resourceName);
		return loader.getFeatureMapping();
	}
}
