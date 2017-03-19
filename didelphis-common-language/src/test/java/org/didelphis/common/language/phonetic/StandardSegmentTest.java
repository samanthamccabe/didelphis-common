/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * http://www.apache.org/licenses/LICENSE-2.0                             *
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
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.didelphis.common.language.phonetic.sequences.SequenceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/14/2015
 */
public class StandardSegmentTest {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(SequenceTest.class);

	private static final SequenceFactory<Double> FACTORY = loadFactory();

	private static final Pattern INFINITY_PATTERN = Pattern.compile("([-+])?Infinity");
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([^\\-])(\\d\\.\\d)");

	@Test
	void testUnderspecifiedSegment01() {
		String string = "[-continuant, +release]";
		Segment<Double> received = FACTORY.getSegment(string);
		Segment<Double> expected = FACTORY.getSegment("[-continuant, +release]");
		Assertions.assertEquals(expected, received);
	}

	@Test
	void testSelectorAliasHigh() {
		Segment<Double> alias = FACTORY.getSegment("[high]");
		Segment<Double> segment = FACTORY.getSegment("[+high]");

		Assertions.assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasMid() {
		Segment<Double> alias = FACTORY.getSegment("[mid]");
		Segment<Double> segment = FACTORY.getSegment("[0:high]");

		Assertions.assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasLow() {
		Segment<Double> alias = FACTORY.getSegment("[low]");
		Segment<Double> segment = FACTORY.getSegment("[-high]");

		Assertions.assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasRetroflex() {
		Segment<Double> alias = FACTORY.getSegment("[retroflex]");
		Segment<Double> segment = FACTORY.getSegment("[4:coronal, -distributed]");

		Assertions.assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasPalatal() {
		Segment<Double> alias = FACTORY.getSegment("[palatal]");
		Segment<Double> segment = FACTORY.getSegment("[4:coronal, +distributed]");

		Assertions.assertTrue(alias.matches(segment));
	}

	@Test
	void testMatch01() {
		Segment<Double> segmentA = FACTORY.getSegment("a");

		Segment<Double> segmentP = FACTORY.getSegment("p");
		Segment<Double> segmentT = FACTORY.getSegment("t");
		Segment<Double> segmentK = FACTORY.getSegment("k");

		Segment<Double> received = FACTORY.getSegment("[-continuant, -son]");

		Assertions.assertTrue(segmentP.matches(received));
		Assertions.assertTrue(segmentT.matches(received));
		Assertions.assertTrue(segmentK.matches(received));

		Assertions.assertTrue(received.matches(segmentP));
		Assertions.assertTrue(received.matches(segmentT));
		Assertions.assertTrue(received.matches(segmentK));

		Assertions.assertFalse(segmentA.matches(received));
		Assertions.assertFalse(received.matches(segmentA));
	}

	@Test
	void testMatch02() {
		Segment<Double> a = FACTORY.getSegment("a");
		Segment<Double> n = FACTORY.getSegment("n");

		Assertions.assertFalse(a.matches(n), "a matches n");
		Assertions.assertFalse(n.matches(a), "n matches a");
	}

	@Test
	void testMatch03() {
		Segment<Double> segment = FACTORY.getSegment(
				"[-con, -hgh, +frn, -atr, +voice]");

		Segment<Double> a = FACTORY.getSegment("a");

		Assertions.assertTrue(a.matches(segment));
		Assertions.assertTrue(segment.matches(a));
	}

	@Test
	void testMatch04() {
		Segment<Double> x = FACTORY.getSegment("x");
		Segment<Double> e = FACTORY.getSegment("e");

		Assertions.assertFalse(e.matches(x));
		Assertions.assertFalse(x.matches(e));
	}

	@Test
	void testOrdering01() {
		Segment<Double> p = FACTORY.getSegment("p");
		Segment<Double> b = FACTORY.getSegment("b");

		Assertions.assertTrue(p.compareTo(b) == -1);
	}

	@Test
	void testOrdering02() {
		Segment<Double> p = FACTORY.getSegment("p");
		Segment<Double> t = FACTORY.getSegment("t");

		Assertions.assertTrue(p.compareTo(t) == 1);
	}

	@Test
	void testConstraintLateralToNasal01() {
		Segment<Double> segment = FACTORY.getSegment("l");

		FeatureArray<Double> features = segment.getFeatures();
		features.set(6, 1.0);

		double received = features.get(5);
		double expected = -1.0;

		Assertions.assertEquals(expected, received, 0.00001);
	}

	@Test
	void testConstraintLateralToNasal02() {
		Segment<Double> segment = FACTORY.getSegment("l");

		Segment<Double> pNas = FACTORY.getSegment("[+nas]");
		Segment<Double> nLat = FACTORY.getSegment("[-lat]");

		assertMatch(segment, pNas, nLat);
	}

	@Test
	void testConstraintNasalToLateral01() {
		Segment<Double> segment = FACTORY.getSegment("n");

		FeatureArray<Double> features = segment.getFeatures();
		features.set(5, 1.0);

		double expected = -1.0;
		double received = features.get(6);

		Assertions.assertEquals(expected, received, 0.00001);
	}

	@Test
	void testConstraintNasalToLateral02() {
		Segment<Double> segment = FACTORY.getSegment("n");

		Segment<Double> pLat = FACTORY.getSegment("[+lat]"); // i = 5
		Segment<Double> nNas = FACTORY.getSegment("[-nas]"); // i = 6

		assertMatch(segment, pLat, nNas);
	}

	@Test
	void testConstaintSonorant() {
		Segment<Double> segment = FACTORY.getSegment("i");

		Segment<Double> nSon = FACTORY.getSegment("[-son]"); // i = 1
		Segment<Double> pCon = FACTORY.getSegment("[+con]"); // i = 0

		assertMatch(segment, nSon, pCon);
	}

	@Test
	void testConstraintConsonant() {
		Segment<Double> segment = FACTORY.getSegment("s");

		Segment<Double> nCon = FACTORY.getSegment("[-con]"); // i = 0
		Segment<Double> pSon = FACTORY.getSegment("[+son]"); // i = 1

		assertMatch(segment, nCon, pSon);
	}

	@Test
	void testConstraintConsonantalRelease() {
		Segment<Double> segment = FACTORY.getSegment("kx");

		Segment<Double> nCon = FACTORY.getSegment("[-con]"); // i = 0
		Segment<Double> nRel = FACTORY.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCon, nRel);
	}

	@Test
	void testConstraintContinuantRelease() {
		Segment<Double> segment = FACTORY.getSegment("kx");

		Segment<Double> nCnt = FACTORY.getSegment("[+cnt]"); // i = 0
		Segment<Double> nRel = FACTORY.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintSonorantRelease() {
		Segment<Double> segment = FACTORY.getSegment("ts");

		Segment<Double> nCnt = FACTORY.getSegment("[+son]"); // i = 0
		Segment<Double> nRel = FACTORY.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintReleaseConsonantal() {
		Segment<Double> segment = FACTORY.getSegment("r");

		Segment<Double> pRel = FACTORY.getSegment("[+rel]"); // i = 0
		Segment<Double> pCon = FACTORY.getSegment("[+con]"); // i = 1

		assertMatch(segment, pRel, pCon);
	}

	private static SequenceFactory<Double> loadFactory() {

		String name = "AT_hybrid.model";
		FileHandler handler = ClassPathFileHandler.INSTANCE;
		FormatterMode mode = FormatterMode.INTELLIGENT;
		DoubleFeatureMapping mapping = DoubleFeatureMapping.load(name, handler, mode);

		return new SequenceFactory<>(mapping, mode);
	}

	private static void assertMatch(Segment<Double> segment, Segment<Double> modifier, Segment<Double> matching) {
		Segment<Double> alter = segment.alter(modifier);
		String message = "\n"
		                 + segment.getFeatures()
		                 + "\naltered by\n"
		                 + modifier.getFeatures()
		                 + "\nproduces\n"
		                 + alter.getFeatures()
		                 + "\nwhich does not match\n"
		                 + matching.getFeatures();

		message = INFINITY_PATTERN.matcher(message).replaceAll("____");
		message = DECIMAL_PATTERN.matcher(message).replaceAll("$1 $2");
		Assertions.assertTrue(alter.matches(matching), message);
	}
}
