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

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.model.FeatureModel;
import org.didelphis.common.language.phonetic.model.StandardFeatureModel;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/5/2015
 */
public class SequenceFactoryTest {

	//TODO: model with specification but no symbols / modifiers
	
	//TODO: model with empty spec but defined symbols / modifiers
	
	@Test
	public void testGetSequence01() throws IOException {
		InputStream stream = SequenceFactoryTest.class.getClassLoader().getResourceAsStream("AT_hybrid.model");
		FormatterMode formatterMode = FormatterMode.INTELLIGENT;
		
		FeatureModel model = new StandardFeatureModel(stream, formatterMode);
		
		String word = "avaːm";

		SequenceFactory factory = new SequenceFactory(model, formatterMode);

		Sequence sequence = factory.getSequence(word);
		assertTrue(!sequence.isEmpty());
	}

	@Test
	public void testReserved() {
		Set<String> reserved = new HashSet<>();
		reserved.add("ph");
		reserved.add("th");
		reserved.add("kh");

		SequenceFactory factory = new SequenceFactory(
			StandardFeatureModel.EMPTY_MODEL,
			new VariableStore(FormatterMode.NONE),
			reserved,
			FormatterMode.NONE);

		Sequence expected = factory.getSequence("");
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("ph"));
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("th"));
		expected.add(factory.getSegment("a"));
		expected.add(factory.getSegment("kh"));
		expected.add(factory.getSegment("a"));

		Sequence received = factory.getSequence("aphathakha");

		assertEquals(expected, received);
	}
}
