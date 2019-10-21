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
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.DoubleFeature;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code GeneralFeatureModelTest}
 *
 * @since 0.1.0
 */
class GeneralFeatureModelTest extends PhoneticTestBase {

	private static FeatureModel<Integer> model;
	private static FeatureModel<Integer> other;
	private static FeatureModel<Integer> empty;

	@BeforeAll
	static void init() {
		model = loader.getFeatureModel();
		other = new FeatureModelLoader<>(IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				"AT_hybrid.model"
		).getFeatureModel();
		empty = new FeatureModelLoader<>(IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				Collections.emptyList(), ""
		).getFeatureModel();
	}

	@Test
	void testToString() {
		assertEquals(model.toString(), other.toString());
		assertNotEquals(model.toString(), empty.toString());
	}

	@Test
	void testHashCode() {
		assertEquals(model.hashCode(), other.hashCode());
		assertNotEquals(model.hashCode(), empty.hashCode());
	}

	@Test
	void testEquals() {
		assertEquals(model, other);
		assertNotEquals(model, empty);
		assertEquals(model, model);
		assertNotEquals(null, model);
		assertNotEquals("null", model);
	}

	@Test
	void getConstraints() {
		assertEquals(model.getConstraints(), other.getConstraints());
		assertNotEquals(model.getConstraints(), empty.getConstraints());
	}

	@Test
	void parseFeatureString() {
		FeatureArray<Integer> arr = model.parseFeatureString("[+consonantal]");
		assertEquals(1,
				(int) arr.get(model.getSpecification().getIndex("consonantal"))
		);
		assertNull(arr.get(model.getSpecification().getIndex("lateral")));
	}

	@Test
	void parseFeatureStringThrowsParseException01() {
		assertThrows(ParseException.class,
				() -> model.parseFeatureString("[?x]")
		);
	}

	@Test
	void parseFeatureStringThrowsParseException02() {
		assertThrows(ParseException.class,
				() -> model.parseFeatureString("[+x]")
		);
	}

	@Test
	void getFeatureType() {
		assertEquals(IntegerFeature.INSTANCE, model.getFeatureType());
		assertNotEquals(DoubleFeature.INSTANCE, model.getFeatureType());
	}

	@Test
	void size() {
		assertEquals(
				model.getSpecification().size(),
				other.getSpecification().size()
		);
		assertNotEquals(
				model.getSpecification().size(),
				empty.getSpecification().size()
		);
	}

	@Test
	void getFeatureIndices() {
		assertEquals(
				model.getSpecification().getFeatureIndices(),
				other.getSpecification().getFeatureIndices()
		);
		assertNotEquals(
				model.getSpecification().getFeatureIndices(),
				empty.getSpecification().getFeatureIndices()
		);
	}

	@Test
	void getIndex() {
		int i = 0;
		for (String name : model.getSpecification().getFeatureNames()) {
			assertEquals(i, model.getSpecification().getIndex(name));
			i++;
		}
	}

	@Test
	void getFeatureNames() {
		assertEquals(
				model.getSpecification().getFeatureNames(),
				other.getSpecification().getFeatureNames()
		);
		assertNotEquals(
				model.getSpecification().getFeatureNames(),
				empty.getSpecification().getFeatureNames()
		);
	}

}
