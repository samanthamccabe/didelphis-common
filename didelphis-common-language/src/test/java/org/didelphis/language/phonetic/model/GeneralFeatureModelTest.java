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
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.ByteFeature;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code GeneralFeatureModelTest}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-15
 */
@SuppressWarnings("ObjectEqualsNull")
class GeneralFeatureModelTest extends PhoneticTestBase {

	private static FeatureModel<Integer> model;
	private static FeatureModel<Integer> other;
	private static FeatureModel<Integer> empty;

	@BeforeAll
	static void init() {
		model = loader.getFeatureModel();
		other = new FeatureModelLoader<>(IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE, "AT_hybrid.model")
				.getFeatureModel();
		empty = new FeatureModelLoader<>(IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE, Collections.emptyList())
				.getFeatureModel();
	}

	@Test
	void testToString() {
		assertEquals(model.toString(),other.toString());
		assertNotEquals(model.toString(),empty.toString());
	}

	@Test
	void testHashCode() {
		assertEquals(model.hashCode(),other.hashCode());
		assertNotEquals(model.hashCode(),empty.hashCode());
	}

	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	@Test
	void testEquals() {
		assertEquals(model, other);
		assertNotEquals(model, empty);
		assertEquals(model, model);
		assertFalse(model.equals(null));
		assertFalse(model.equals("null"));
	}

	@Test
	void getConstraints() {
		assertEquals(model.getConstraints(), other.getConstraints());
		assertNotEquals(model.getConstraints(), empty.getConstraints());
	}

	@Test
	void parseFeatureString() {
		FeatureArray<Integer> arr = model.parseFeatureString("[+consonantal]");
		assertEquals(1, (int) arr.get(model.getSpecification().getIndex("consonantal")));
		assertNull(arr.get(model.getSpecification().getIndex("lateral")));
	}

	@Test
	void parseFeatureString_ParseException() {
		assertThrows(ParseException.class, ()-> model.parseFeatureString("[?x]"));
	}

	@Test
	void parseFeatureString_IllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> model.parseFeatureString("[+x]"));
	}

	@Test
	void getFeatureType() {
		assertEquals(IntegerFeature.INSTANCE, model.getFeatureType());
		assertNotEquals(ByteFeature.INSTANCE, model.getFeatureType());
	}

	@Test
	void size() {
		assertEquals(model.getSpecification().size(), other.getSpecification().size());
		assertNotEquals(model.getSpecification().size(), empty.getSpecification().size());
	}

	@Test
	void getFeatureIndices() {
		assertEquals(model.getSpecification().getFeatureIndices(), other.getSpecification().getFeatureIndices());
		assertNotEquals(model.getSpecification().getFeatureIndices(), empty.getSpecification().getFeatureIndices());
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
		assertEquals(model.getSpecification().getFeatureNames(), other.getSpecification().getFeatureNames());
		assertNotEquals(model.getSpecification().getFeatureNames(), empty.getSpecification().getFeatureNames());
	}

}