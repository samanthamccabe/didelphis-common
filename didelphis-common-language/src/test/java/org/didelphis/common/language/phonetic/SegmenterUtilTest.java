package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
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

	private static final FeatureMapping<Byte> MAPPING = EmptyFeatureMapping.BYTE;

	@Test
	void testLongVarName() {
		String word = "[OBSTRUENT]";
		
		List<String> reserved = new ArrayList<>();
		reserved.add(word);
		
		FormatterMode mode = FormatterMode.COMPOSITION;
		Sequence<Byte> sequence = SegmenterUtil.getSequence(word, MAPPING, reserved, mode);
		Sequence<Byte> expected = new BasicSequence<>(MAPPING.getFeatureModel());
		expected.add(MAPPING.getSegment(word));
		
		Assertions.assertEquals(expected, sequence);
	}
	
	@Test
	void testGetSequence_Composition() {
		String word = "word";
		FormatterMode mode = FormatterMode.COMPOSITION;
		Sequence<Byte> sequence = SegmenterUtil.getSequence(word, MAPPING, null, mode);
		Sequence<Byte> expected = getSequence(MAPPING, "w", "o", "r", "d");
		
		Assertions.assertEquals(expected, sequence);
	}
	
	private static <N> Sequence<N> getSequence(FeatureMapping<N> model, String...strings) {
		Sequence<N> segments = new BasicSequence<>(model.getFeatureModel());
		for (String string : strings) {
			segments.add(SegmenterUtil.getSegment(string, model, FormatterMode.COMPOSITION));
		}
		return segments;
	}
}
