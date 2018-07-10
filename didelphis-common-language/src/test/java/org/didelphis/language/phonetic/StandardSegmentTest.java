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

package org.didelphis.language.phonetic;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samantha Fiona McCabe
 * @date 2/14/2015
 */
public class StandardSegmentTest extends PhoneticTestBase {
	
	private static final Pattern INFINITY_PATTERN = Pattern.compile("([-+])?Infinity");
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([^\\-])(\\d\\.\\d)");

	@Test
	void testUnderspecifiedSegment01() {
		String string = "[-continuant, +release]";
		Segment<Integer> received = factory.toSegment(string);
		Segment<Integer> expected = factory.toSegment("[-continuant, +release]");
		assertEquals(expected, received);
	}

	@Test
	void testSelectorAliasHigh() {
		Segment<Integer> alias = factory.toSegment("[high]");
		Segment<Integer> segment = factory.toSegment("[+high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasMid() {
		Segment<Integer> alias = factory.toSegment("[mid]");
		Segment<Integer> segment = factory.toSegment("[0:high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasLow() {
		Segment<Integer> alias = factory.toSegment("[low]");
		Segment<Integer> segment = factory.toSegment("[-high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasRetroflex() {
		Segment<Integer> alias = factory.toSegment("[retroflex]");
		Segment<Integer> segment = factory.toSegment("[4:coronal, -distributed]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasPalatal() {
		Segment<Integer> alias = factory.toSegment("[palatal]");
		Segment<Integer> segment = factory.toSegment("[4:coronal, +distributed]");

		assertMatches(alias, segment);
	}

	@Test
	void testMatch01() {
		Segment<Integer> segmentA = factory.toSegment("a");

		Segment<Integer> segmentP = factory.toSegment("p");
		Segment<Integer> segmentT = factory.toSegment("t");
		Segment<Integer> segmentK = factory.toSegment("k");

		Segment<Integer> received = factory.toSegment("[-continuant, -son]");

		assertMatches(segmentP, received);
		assertMatches(segmentT, received);
		assertMatches(segmentK, received);

		assertMatches(received, segmentP);
		assertMatches(received, segmentT);
		assertMatches(received, segmentK);

		assertNotMatches(segmentA, received);
		assertFalse(received.matches(segmentA));
	}

	@Test
	void testMatch02() {
		Segment<Integer> g1 = factory.toSegment("a");
		Segment<Integer> g2 = factory.toSegment("n");

		assertFalse(g1.matches(g2), "a matches n");
		assertFalse(g2.matches(g1), "n matches a");
	}

	@Test
	void testFeatureMatch() {
		Segment<Integer> g = factory.toSegment("[-con -hgh +frn -atr +voice]");
		Segment<Integer> a = factory.toSegment("a");

		assertMatches(a, g);
		assertMatches(g, a);
	}
	
	@Test
	void testMatch04() {
		Segment<Integer> x = factory.toSegment("x");
		Segment<Integer> e = factory.toSegment("e");

		assertFalse(e.matches(x));
		assertFalse(x.matches(e));
	}
	
	@Test
	void testSegmentsNotInModel() {
		Segment<Integer> z1 = factory.toSegment("ʓ");
		Segment<Integer> z2 = factory.toSegment("ʓ");
		Segment<Integer> a = factory.toSegment("a");

		// Make sure that 
		assertNotMatches(z2, a);
		assertNotMatches(a, z2);
		
		assertMatches(z2, z1);
		assertMatches(z1, z2);
	}
	
	@Test
	void testOrdering01() {
		Segment<Integer> p = factory.toSegment("p");
		Segment<Integer> b = factory.toSegment("b");

		assertEquals(-1, p.compareTo(b));
	}
	
	@Test
	void testOrdering02() {
		Segment<Integer> p = factory.toSegment("p");
		Segment<Integer> t = factory.toSegment("t");

		assertEquals(1, p.compareTo(t));
	}

	@Test
	void testConstraintLateralToNasal01() {
		Segment<Integer> segment = factory.toSegment("l");

		FeatureArray<Integer> features = segment.getFeatures();
		features.set(6, 1);

		int received = features.get(5);
		int expected = -1;

		assertEquals(expected, received, 0.001);
	}

	@Test
	void testConstraintLateralToNasal02() {
		Segment<Integer> segment = factory.toSegment("l");

		Segment<Integer> pNas = factory.toSegment("[+nas]");
		Segment<Integer> nLat = factory.toSegment("[-lat]");

		assertMatch(segment, pNas, nLat);
	}

	@Test
	void testConstraintNasalToLateral01() {
		Segment<Integer> segment = factory.toSegment("n");

		FeatureArray<Integer> features = segment.getFeatures();
		features.set(5, 1);

		int expected = -1;
		int received = features.get(6);

		assertEquals(expected, received);
	}

	@Test
	void testConstraintNasalToLateral02() {
		Segment<Integer> segment = factory.toSegment("n");

		Segment<Integer> pLat = factory.toSegment("[+lat]"); // i = 5
		Segment<Integer> nNas = factory.toSegment("[-nas]"); // i = 6

		assertMatch(segment, pLat, nNas);
	}

	@Test
	void testConstaintSonorant() {
		Segment<Integer> segment = factory.toSegment("i");

		Segment<Integer> nSon = factory.toSegment("[-son]"); // i = 1
		Segment<Integer> pCon = factory.toSegment("[+con]"); // i = 0

		assertMatch(segment, nSon, pCon);
	}

	@Test
	void testConstraintConsonant() {
		Segment<Integer> segment = factory.toSegment("s");

		Segment<Integer> nCon = factory.toSegment("[-con]"); // i = 0
		Segment<Integer> pSon = factory.toSegment("[+son]"); // i = 1

		assertMatch(segment, nCon, pSon);
	}

	@Test
	void testConstraintConsonantalRelease() {
		Segment<Integer> segment = factory.toSegment("kx");

		Segment<Integer> nCon = factory.toSegment("[-con]"); // i = 0
		Segment<Integer> nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCon, nRel);
	}

	@Test
	void testConstraintContinuantRelease() {
		Segment<Integer> segment = factory.toSegment("kx");

		Segment<Integer> nCnt = factory.toSegment("[+cnt]"); // i = 0
		Segment<Integer> nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintSonorantRelease() {
		Segment<Integer> segment = factory.toSegment("ts");

		Segment<Integer> nCnt = factory.toSegment("[+son]"); // i = 0
		Segment<Integer> nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintReleaseConsonantal() {
		Segment<Integer> segment = factory.toSegment("r");

		Segment<Integer> pRel = factory.toSegment("[+rel]"); // i = 0
		Segment<Integer> pCon = factory.toSegment("[+con]"); // i = 1

		assertMatch(segment, pRel, pCon);
	}

	private static <T> void assertMatches(Segment<T> x, Segment<T> y) {
		assertTrue(x.matches(y));
	}

	private static <T> void assertNotMatches(Segment<T> x, Segment<T> y) {
		assertFalse(x.matches(y));
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

	private static void assertMatch(
			Segment<Integer> segment,
			Segment<Integer> modifier,
			Segment<Integer> matching
	) {
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
