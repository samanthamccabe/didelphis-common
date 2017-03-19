/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.phonetic;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/5/2015
 */
public class SequenceFactoryTest {

	@Test
	void testGetSequence01() throws IOException {
		String name = "AT_hybrid.model";
		
		FormatterMode formatterMode = FormatterMode.INTELLIGENT;
		
		FileHandler handler = ClassPathFileHandler.INSTANCE;

		String word = "avaːm";

		DoubleFeatureMapping mapping = DoubleFeatureMapping.load(name,
				handler,
				formatterMode);

		SequenceFactory<Double> factory = new SequenceFactory<>(mapping, formatterMode);

		Sequence<Double> sequence = factory.getSequence(word);
		Assertions.assertTrue(!sequence.isEmpty());
	}

	@Test
	void testReserved() {
		Set<String> reserved = new HashSet<>();
		reserved.add("ph");
		reserved.add("th");
		reserved.add("kh");

		SequenceFactory<Double> factory = new SequenceFactory<>(
				EmptyFeatureMapping.DOUBLE, 
				new VariableStore(FormatterMode.NONE),
			reserved,
			FormatterMode.NONE);

		Sequence<Double> expected = factory.getSequence("");
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("ph"));
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("th"));
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("kh"));
		expected.add(factory.getSegment("a"));

		Sequence received = factory.getSequence("aphathakha");

		Assertions.assertEquals(expected, received);
	}
}
