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
import org.didelphis.io.NullFileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.IntegerFeature;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeatureModelLoaderTest extends PhoneticTestBase {

	private static final FeatureModelLoader LOADER
			= IntegerFeature.INSTANCE.emptyLoader();

	@Test
	void testConstructorFeatureType() {
		FeatureModelLoader loader1 = new FeatureModelLoader();

		assertEquals(LOADER, loader1);

		assertEquals(LOADER.hashCode(), loader1.hashCode());

		assertEquals(LOADER.toString(), loader1.toString());
	}

	@Test
	void testConstructorHandlerAndPath() {
		String path = "AT_hybrid.model";
		FeatureModelLoader loader1 = new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				path
		);

		assertEquals(loader, loader1);

		assertEquals(loader.hashCode(), loader1.hashCode());

		assertEquals(loader.toString(), loader1.toString());
	}

	@Test
	void testParseTooFewFeatures() {
		List<String> lines = Arrays.asList("FEATURES",
				"foo\tfoo\tbinary",
				"bar\tbar\tbinary",
				"baz\tbaz",
				"SYMBOLS",
				"w\t+\t+"
		);

		assertThrows(ParseException.class, () -> new FeatureModelLoader(
				NullFileHandler.INSTANCE,
				lines,
				""
		));
	}

	@Test
	void testParseInvalidFeature() {
		List<String> lines = Arrays.asList("FEATURES", "foo\tfoo\tbinary", ";");

		assertThrows(ParseException.class, () -> new FeatureModelLoader(
				NullFileHandler.INSTANCE,
				lines,
				""
		));
	}

	@Test
	void testConstructorWithBadPath() {
		assertThrows(ParseException.class, () -> new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				"bad_link"
		));
	}

	@Test
	void testBadImport() {
		List<String> lines = Collections.singletonList("import 'bad_link'");

		assertThrows(ParseException.class, () -> new FeatureModelLoader(
				ClassPathFileHandler.INSTANCE,
				lines,
				""
		));
	}

	@Test
	void testConstructorHandlerAndLines() {
		List<String> lines = Arrays.asList("FEATURES",
				"foo\tfoo\tbinary",
				"bar\tbar\tbinary",
				"SYMBOLS",
				"w\t+\t+",
				"x\t+\t-",
				"y\t-\t+",
				"z\t-\t-",
				"a\t-\t-",
				"\t\t-\t-",
				"MODIFIERS      ",
				".\t-\t",
				",\t\t-",
				"`\t+\t",
				"^\t\t+",
				"\t\t-\t-"
		);

		FeatureModelLoader loader1 = new FeatureModelLoader(
				NullFileHandler.INSTANCE,
				lines,
				""
		);

		FeatureSpecification specification = loader1.getSpecification();
		assertEquals(2, specification.size());
		assertEquals(0, specification.getIndex("foo"));
		assertEquals(1, specification.getIndex("bar"));

		FeatureMapping mapping = loader1.getFeatureMapping();
		assertTrue(mapping.containsKey("w"));
		assertTrue(mapping.containsKey("x"));
		assertTrue(mapping.containsKey("y"));
		assertTrue(mapping.containsKey("z"));
		assertFalse(mapping.containsKey("a"));
		assertFalse(mapping.containsKey("b"));
		assertFalse(mapping.containsKey("c"));
	}

	@Test
	void parseConstraint() {
		String entry = "[+lateral] > [-nasal]";
		Constraint constraint = loader.parseConstraint(entry);

		FeatureSpecification specification = loader.getSpecification();

		int lateralIndex = specification.getIndex("lateral");
		int nasalIndex = specification.getIndex("nasal");
		assertEquals(1, (int) constraint.getSource().get(lateralIndex));
		assertEquals(-1, (int) constraint.getTarget().get(nasalIndex));
	}

	@Test
	void getSpecification() {
		assertEquals(0, LOADER.getSpecification().size());
	}

	@Test
	void getFeatureModel() {
		FeatureType featureType = LOADER.getFeatureModel().getFeatureType();
		assertEquals(IntegerFeature.INSTANCE, featureType);
	}

	@Test
	void getFeatureMapping() {
		assertTrue(LOADER.getFeatureMapping().getFeatureMap().isEmpty());
	}
}
