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
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.SemidefinedSegment;
import org.didelphis.language.phonetic.segments.UndefinedSegment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneralFeatureMappingTest extends PhoneticTestBase {

	private static FeatureMapping mapping;

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
		FeatureMapping model = loadMapping(resourceName);
		assertNotNull(model.getSpecification());
		assertTrue(model.getSpecification().size() > 0);
	}

	@Test
	void testLoad_AT_Hybrid() {
		String resourceName = "AT_hybrid.model";
		FeatureMapping model = loadMapping(resourceName);
		assertFalse(model.getFeatureMap().isEmpty());
		assertFalse(model.getModifiers().isEmpty());
	}

	@Test
	void testContainsKey() {
		assertTrue(mapping.containsKey("p"));
		assertFalse(mapping.containsKey("@"));
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
		Segment s1 = mapping.parseSegment("ts");
		assertFalse(s1 instanceof UndefinedSegment);
		Segment s2 = mapping.parseSegment("t͡s");
		assertFalse(s2 instanceof UndefinedSegment);
		Segment s3 = mapping.parseSegment("t͜s");
		assertFalse(s3 instanceof UndefinedSegment);

		assertEquals(s1.getFeatures(), s2.getFeatures());
		assertEquals(s1.getFeatures(), s3.getFeatures());
	}

	@Test
	void testParseSegmentMissingModifier() {
		Segment segment1 = mapping.parseSegment("n");
		Segment segment2 = mapping.parseSegment("n᷄");
		assertFalse(segment1 instanceof SemidefinedSegment);
		assertTrue(segment2 instanceof SemidefinedSegment);
	}

	private static void testBestSymbol(String string) {
		Segment segment = mapping.parseSegment(string);
		FeatureArray array = segment.getFeatures();
		String bestSymbol = mapping.findBestSymbol(array);
		assertEquals(string, bestSymbol);
	}

	private static FeatureMapping loadMapping(String resourceName) {
		FeatureModelLoader loader = new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				resourceName);
		return loader.getFeatureMapping();
	}
}
