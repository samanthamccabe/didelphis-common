package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.model.FeatureModel;
import org.didelphis.common.language.phonetic.model.StandardFeatureModel;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by samantha on 1/28/17.
 */
public class SegmenterUtilTest {

	private static final FeatureModel DEFAULT_MODEL = StandardFeatureModel.EMPTY_MODEL;

	@Test
	public void testGetSequence_Composition() {
		String word = "word";
		FormatterMode mode = FormatterMode.COMPOSITION;
		Sequence sequence = SegmenterUtil.getSequence(word, DEFAULT_MODEL, null, mode);
		Sequence expected = getSequence(DEFAULT_MODEL,
				"w", "o", "r", "d");
		
		assertEquals(expected, sequence);
	}
	
	private static BasicSequence getSequence(FeatureModel model, String...strings) {
		BasicSequence segments = new BasicSequence(model.getSpecification());
		for (String string : strings) {
			segments.add(model.getSegment(string));
		}
		return segments;
	}
}
