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

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.BasicSequenceTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 * Date: 2/14/2015
 */
public class StandardSegmentTest extends PhoneticTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(BasicSequenceTest.class);

	private static final Pattern INFINITY_PATTERN = Pattern.compile("([-+])?Infinity");
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([^\\-])(\\d\\.\\d)");

	@Test
	void testUnderspecifiedSegment01() {
		String string = "[-continuant, +release]";
		Segment<Integer> received = factory.getSegment(string);
		Segment<Integer> expected = factory.getSegment("[-continuant, +release]");
		assertEquals(expected, received);
	}

	@Test
	void testSelectorAliasHigh() {
		Segment<Integer> alias = factory.getSegment("[high]");
		Segment<Integer> segment = factory.getSegment("[+high]");

		assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasMid() {
		Segment<Integer> alias = factory.getSegment("[mid]");
		Segment<Integer> segment = factory.getSegment("[0:high]");

		assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasLow() {
		Segment<Integer> alias = factory.getSegment("[low]");
		Segment<Integer> segment = factory.getSegment("[-high]");

		assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasRetroflex() {
		Segment<Integer> alias = factory.getSegment("[retroflex]");
		Segment<Integer> segment = factory.getSegment("[4:coronal, -distributed]");

		assertTrue(alias.matches(segment));
	}

	@Test
	void testSelectorAliasPalatal() {
		Segment<Integer> alias = factory.getSegment("[palatal]");
		Segment<Integer> segment = factory.getSegment("[4:coronal, +distributed]");

		assertTrue(alias.matches(segment));
	}

	@Test
	void testMatch01() {
		Segment<Integer> segmentA = factory.getSegment("a");

		Segment<Integer> segmentP = factory.getSegment("p");
		Segment<Integer> segmentT = factory.getSegment("t");
		Segment<Integer> segmentK = factory.getSegment("k");

		Segment<Integer> received = factory.getSegment("[-continuant, -son]");

		assertTrue(segmentP.matches(received));
		assertTrue(segmentT.matches(received));
		assertTrue(segmentK.matches(received));

		assertTrue(received.matches(segmentP));
		assertTrue(received.matches(segmentT));
		assertTrue(received.matches(segmentK));

		assertFalse(segmentA.matches(received));
		assertFalse(received.matches(segmentA));
	}

	@Test
	void testMatch02() {
		Segment<Integer> a = factory.getSegment("a");
		Segment<Integer> n = factory.getSegment("n");

		assertFalse(a.matches(n), "a matches n");
		assertFalse(n.matches(a), "n matches a");
	}

	@Test
	void testMatch03() {
		Segment<Integer> segment = factory.getSegment(
				"[-con, -hgh, +frn, -atr, +voice]");

		Segment<Integer> a = factory.getSegment("a");

		assertTrue(a.matches(segment));
		assertTrue(segment.matches(a));
	}

	@Test
	void testMatch04() {
		Segment<Integer> x = factory.getSegment("x");
		Segment<Integer> e = factory.getSegment("e");

		assertFalse(e.matches(x));
		assertFalse(x.matches(e));
	}

	@Test
	void testOrdering01() {
		Segment<Integer> p = factory.getSegment("p");
		Segment<Integer> b = factory.getSegment("b");

		assertEquals(-1, p.compareTo(b));
	}

	@Test
	void testOrdering02() {
		Segment<Integer> p = factory.getSegment("p");
		Segment<Integer> t = factory.getSegment("t");

		assertEquals(1, p.compareTo(t));
	}

	@Test
	void testConstraintLateralToNasal01() {
		Segment<Integer> segment = factory.getSegment("l");

		FeatureArray<Integer> features = segment.getFeatures();
		features.set(6, 1);

		int received = features.get(5);
		int expected = -1;

		assertEquals(expected, received, 00001);
	}

	@Test
	void testConstraintLateralToNasal02() {
		Segment<Integer> segment = factory.getSegment("l");

		Segment<Integer> pNas = factory.getSegment("[+nas]");
		Segment<Integer> nLat = factory.getSegment("[-lat]");

		assertMatch(segment, pNas, nLat);
	}

	@Test
	void testConstraintNasalToLateral01() {
		Segment<Integer> segment = factory.getSegment("n");

		FeatureArray<Integer> features = segment.getFeatures();
		features.set(5, 1);

		int expected = -1;
		int received = features.get(6);

		assertEquals(expected, received);
	}

	@Test
	void testConstraintNasalToLateral02() {
		Segment<Integer> segment = factory.getSegment("n");

		Segment<Integer> pLat = factory.getSegment("[+lat]"); // i = 5
		Segment<Integer> nNas = factory.getSegment("[-nas]"); // i = 6

		assertMatch(segment, pLat, nNas);
	}

	@Test
	void testConstaintSonorant() {
		Segment<Integer> segment = factory.getSegment("i");

		Segment<Integer> nSon = factory.getSegment("[-son]"); // i = 1
		Segment<Integer> pCon = factory.getSegment("[+con]"); // i = 0

		assertMatch(segment, nSon, pCon);
	}

	@Test
	void testConstraintConsonant() {
		Segment<Integer> segment = factory.getSegment("s");

		Segment<Integer> nCon = factory.getSegment("[-con]"); // i = 0
		Segment<Integer> pSon = factory.getSegment("[+son]"); // i = 1

		assertMatch(segment, nCon, pSon);
	}

	@Test
	void testConstraintConsonantalRelease() {
		Segment<Integer> segment = factory.getSegment("kx");

		Segment<Integer> nCon = factory.getSegment("[-con]"); // i = 0
		Segment<Integer> nRel = factory.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCon, nRel);
	}

	@Test
	void testConstraintContinuantRelease() {
		Segment<Integer> segment = factory.getSegment("kx");

		Segment<Integer> nCnt = factory.getSegment("[+cnt]"); // i = 0
		Segment<Integer> nRel = factory.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintSonorantRelease() {
		Segment<Integer> segment = factory.getSegment("ts");

		Segment<Integer> nCnt = factory.getSegment("[+son]"); // i = 0
		Segment<Integer> nRel = factory.getSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintReleaseConsonantal() {
		Segment<Integer> segment = factory.getSegment("r");

		Segment<Integer> pRel = factory.getSegment("[+rel]"); // i = 0
		Segment<Integer> pCon = factory.getSegment("[+con]"); // i = 1

		assertMatch(segment, pRel, pCon);
	}

	private static SequenceFactory<Integer> loadFactory() {
		String path = "AT_hybrid.model";
		FileHandler handler = ClassPathFileHandler.INSTANCE;
		FormatterMode mode = FormatterMode.INTELLIGENT;

		return new SequenceFactory<>(new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				path).getFeatureMapping(), mode);
	}

	private static void assertMatch(Segment<Integer> segment, Segment<Integer> modifier, Segment<Integer> matching) {
		Segment<Integer> alter= new StandardSegment<>(segment);
		alter.alter(modifier);
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
		assertTrue(alter.matches(matching), message);
	}
}
