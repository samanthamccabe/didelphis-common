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

import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/16/17.
 */
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

		//noinspection ObjectEqualsNull
		assertFalse(constraint1.equals(null));

		//noinspection EqualsBetweenInconvertibleTypes
		assertFalse(constraint1.equals("null"));
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

}
