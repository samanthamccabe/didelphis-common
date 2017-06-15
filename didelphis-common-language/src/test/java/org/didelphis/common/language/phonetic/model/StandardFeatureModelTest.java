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

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Samantha Fiona Morrigan McCabe
 */
public class StandardFeatureModelTest extends ModelTestBase {

	private static final FeatureMapping<Integer> MAPPING = loadMapping("AT_hybrid.model", FormatterMode.INTELLIGENT);

	@Test
	void testLoad01() {
		Assertions.assertFalse(MAPPING.getFeatureMap().isEmpty());
		Assertions.assertFalse(MAPPING.getModifiers().isEmpty());
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

	static void testNaN(double v) {
		Assertions.assertTrue(Double.isNaN(v), "Value was " + v + " not NaN");
	}
	
	private static void testBestSymbol(String string) {
		Segment<Integer> segment = MAPPING.parseSegment(string);
		FeatureArray<Integer> array = segment.getFeatures();
		String bestSymbol = MAPPING.findBestSymbol(array);
		Assertions.assertEquals(string, bestSymbol);
	}
}
