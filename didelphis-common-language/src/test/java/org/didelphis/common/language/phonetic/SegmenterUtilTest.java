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

package org.didelphis.common.language.phonetic;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.IntegerFeature;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samantha on 1/28/17.
 */
public class SegmenterUtilTest {

	private static final FeatureMapping<Integer> MAPPING =  new FeatureModelLoader<>(
			IntegerFeature.INSTANCE,
			ClassPathFileHandler.INSTANCE, "AT_hybrid.mapping").getFeatureMapping();

	@Test
	void testLongVarName() {
		String word = "[+sonorant]";
		
		List<String> reserved = new ArrayList<>();
		reserved.add(word);
		
		FormatterMode mode = FormatterMode.COMPOSITION;
		Sequence<Integer> sequence = SegmenterUtil.getSequence(word, MAPPING, reserved, mode);
		Sequence<Integer> expected = new BasicSequence<>(MAPPING.getFeatureModel());
		expected.add(MAPPING.parseSegment(word));
		
		Assertions.assertEquals(expected, sequence);
	}
	
	@Test
	void testGetSequence_Composition() {
		String word = "word";
		FormatterMode mode = FormatterMode.COMPOSITION;
		Sequence<Integer> sequence = SegmenterUtil.getSequence(word, MAPPING, null, mode);
		Sequence<Integer> expected = getSequence(MAPPING, "w", "o", "r", "d");
		
		Assertions.assertEquals(expected, sequence);
	}
	
	private static <T> Sequence<T> getSequence(FeatureMapping<T> model, String...strings) {
		Sequence<T> segments = new BasicSequence<>(model.getFeatureModel());
		for (String string : strings) {
			segments.add(SegmenterUtil.getSegment(string, model, FormatterMode.COMPOSITION));
		}
		return segments;
	}
}
