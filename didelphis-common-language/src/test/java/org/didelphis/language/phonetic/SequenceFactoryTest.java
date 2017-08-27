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

package org.didelphis.language.phonetic;

import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samantha Fiona McCabe
 * @date 2/5/2015
 */
public class SequenceFactoryTest extends PhoneticTestBase{

	@Test
	void testGetSequence01() throws IOException {
		String word = "avaːm";
		Sequence<Integer> sequence = factory.toSequence(word);
		assertTrue(!sequence.isEmpty());
	}

	@Test
	void testReserved() {
		Set<String> reserved = new HashSet<>();
		reserved.add("ph");
		reserved.add("th");
		reserved.add("kh");

		SequenceFactory<Integer> factory = new SequenceFactory<>(
				loader.getFeatureMapping(),
			reserved,
			FormatterMode.NONE);

		Sequence<Integer> expected = factory.toSequence("");
		expected.add(factory.toSegment("a"));
		expected.add(factory.toSegment("ph"));
		expected.add(factory.toSegment("a"));
		expected.add(factory.toSegment("th"));
		expected.add(factory.toSegment("a"));
		expected.add(factory.toSegment("kh"));
		expected.add(factory.toSegment("a"));

		Sequence<Integer> received = factory.toSequence("aphathakha");

		for (int i = 0; i < expected.size(); i++) {
			Segment<Integer> ex = expected.get(i);
			Segment<Integer> re = received.get(i);
			assertEquals(ex, re, "index: "+i);
		}

		assertEquals(expected, received);
	}
}
