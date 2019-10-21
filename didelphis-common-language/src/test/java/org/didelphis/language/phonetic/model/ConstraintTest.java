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

import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.features.SparseFeatureArray;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ConstraintTest extends PhoneticTestBase {

	private static FeatureModel<Integer> model;
	private static Constraint<Integer> constraint1;
	private static Constraint<Integer> constraint2;

	@BeforeAll
	static void init() {
		model = loader.getFeatureModel();

		constraint1 = loader.parseConstraint("[+eje]>[+con,-son,-cnt,-vce]");
		constraint2 = loader.parseConstraint("[+creaky] >[-breathy,+voice]");
	}

	@Test
	void getTarget() {
		String features = "[+con,-son,-cnt,-vce]";
		FeatureArray<Integer> array = model.parseFeatureString(features);
		FeatureArray<Integer> target = constraint1.getTarget();
		assertEquals(target, array);

		FeatureArray<Integer> featureArray = new SparseFeatureArray<>(model);
		featureArray.set(0, 1);
		featureArray.set(1, -1);
		featureArray.set(2, -1);
		featureArray.set(14, -1);
		assertEquals(target, featureArray);
	}

	@Test
	void getSource() {
		String features = "[+eje]";
		FeatureArray<Integer> array = model.parseFeatureString(features);
		FeatureArray<Integer> source = constraint1.getSource();
		assertEquals(source, array);

		FeatureArray<Integer> featureArray = new SparseFeatureArray<>(model);
		featureArray.set(3, 1);
		assertEquals(source, featureArray);
	}

	@Test
	void equals() {
		assertEquals(constraint1, new Constraint<>(constraint1));
		assertEquals(constraint2, new Constraint<>(constraint2));

		assertNotEquals(constraint1, constraint2);

		assertNotEquals(null, constraint1);

		assertNotEquals("null", constraint1);
	}

	@Test
	void testHashCode() {
		assertEquals(constraint1.hashCode(), new Constraint<>(constraint1).hashCode());
		assertEquals(constraint2.hashCode(), new Constraint<>(constraint2).hashCode());
		assertNotEquals(constraint1.hashCode(), constraint2.hashCode());
	}

	@Test
	void testToString() {
		assertEquals(constraint1.toString(), new Constraint<>(constraint1).toString());
		assertEquals(constraint2.toString(), new Constraint<>(constraint2).toString());
		assertNotEquals(constraint1.toString(), constraint2.toString());
	}

	@Test
	void getFeatureModel() {
		assertEquals(model, constraint1.getFeatureModel());
	}

	@Test
	void getSpecification() {
		assertEquals(model.getSpecification(), constraint1.getSpecification());
	}

	@Test
	void testConsistencyFailure() {

		FeatureModelLoader<Integer> loader1 = IntegerFeature.INSTANCE.emptyLoader();
		SequenceFactory<Integer> factory1 = new SequenceFactory<>(
				loader1.getFeatureMapping(),
				FormatterMode.NONE
		);

		FeatureArray<Integer> segment1 = factory.toSegment("a").getFeatures();
		FeatureArray<Integer> segment2 = factory1.toSegment("x").getFeatures();

		assertThrows(
				IllegalArgumentException.class,
				() -> new Constraint<>(segment1, segment2)
		);
	}
}
