/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.phonetic;

import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class StandardSegmentTest extends PhoneticTestBase {

	private static final Pattern INFINITY_PATTERN = Pattern.compile("([-+])?Infinity");
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([^\\-])(\\d\\.\\d)");

	@Test
	void testUnderspecifiedSegment01() {
		String string = "[-continuant, +release]";
		Segment received = factory.toSegment(string);
		Segment expected = factory.toSegment("[-continuant, +release]");
		assertEquals(expected, received);
	}

	@Test
	void testSelectorAliasHigh() {
		Segment alias = factory.toSegment("[high]");
		Segment segment = factory.toSegment("[+high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasMid() {
		Segment alias = factory.toSegment("[mid]");
		Segment segment = factory.toSegment("[0:high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasLow() {
		Segment alias = factory.toSegment("[low]");
		Segment segment = factory.toSegment("[-high]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasRetroflex() {
		Segment alias = factory.toSegment("[retroflex]");
		Segment segment = factory.toSegment("[4:coronal, -distributed]");

		assertMatches(alias, segment);
	}

	@Test
	void testSelectorAliasPalatal() {
		Segment alias = factory.toSegment("[palatal]");
		Segment segment = factory.toSegment("[4:coronal, +distributed]");

		assertMatches(alias, segment);
	}

	@Test
	void testMatch01() {
		Segment segmentA = factory.toSegment("a");

		Segment segmentP = factory.toSegment("p");
		Segment segmentT = factory.toSegment("t");
		Segment segmentK = factory.toSegment("k");

		Segment received = factory.toSegment("[-continuant, -son]");

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
		Segment g1 = factory.toSegment("a");
		Segment g2 = factory.toSegment("n");

		assertFalse(g1.matches(g2), "a matches n");
		assertFalse(g2.matches(g1), "n matches a");
	}

	@Test
	void testFeatureMatch() {
		Segment g = factory.toSegment("[-con -hgh +frn -atr +voice]");
		Segment a = factory.toSegment("a");

		assertMatches(a, g);
		assertMatches(g, a);
	}

	@Test
	void testMatch04() {
		Segment x = factory.toSegment("x");
		Segment e = factory.toSegment("e");

		assertFalse(e.matches(x));
		assertFalse(x.matches(e));
	}

	@Test
	void testSegmentsNotInModel() {
		Segment z1 = factory.toSegment("ʓ");
		Segment z2 = factory.toSegment("ʓ");
		Segment a = factory.toSegment("a");

		// Make sure that
		assertNotMatches(z2, a);
		assertNotMatches(a, z2);

		assertMatches(z2, z1);
		assertMatches(z1, z2);
	}

	@Test
	void testOrdering01() {
		Segment p = factory.toSegment("p");
		Segment b = factory.toSegment("b");

		assertEquals(-1, p.compareTo(b));
	}

	@Test
	void testOrdering02() {
		Segment p = factory.toSegment("p");
		Segment t = factory.toSegment("t");

		assertEquals(1, p.compareTo(t));
	}

	@Test
	void testOrdering03() {
		Segment t1 = factory.toSegment("t");
		Segment t2 = factory.toSegment("t");

		assertEquals(0, t1.compareTo(t2));
		assertEquals(0, t2.compareTo(t1));
	}

	@Test
	void testOrdering04() {
		Segment t1 = factory.toSegment("t");
		Segment t2 = new StandardSegment("x", t1.getFeatures());
		Segment t3 = factory.toSegment("t");

		assertEquals(-1, t1.compareTo(t2));
		assertEquals(1,  t2.compareTo(t1));
		assertEquals(0,  t3.compareTo(t1));
	}

	@Test
	void testOrdering05() {
		// Tests a rare case where two objects with the same symbol are not
		// equal to one another but still have the same feature values. In this
		// case we achieve it by (mis)using SparseFeatureArray to construct an
		// altered copy of the original segment; same symbol and features, but
		// different FeatureArray implementations
		Segment t1 = factory.toSegment("t");

		FeatureArray f1 = t1.getFeatures();
		FeatureArray f2 = new SparseFeatureArray(f1);

		StandardSegment t2 = new StandardSegment("t", f2);

		assertEquals(0, t1.compareTo(t2));
		assertEquals(0, t2.compareTo(t1));
	}

	@Test
	void testConstraintLateralToNasal01() {
		Segment segment = factory.toSegment("l");

		FeatureArray features = segment.getFeatures();
		features.set(6, 1);

		int received = features.get(5);
		int expected = -1;

		assertEquals(expected, received, 0.001);
	}

	@Test
	void testConstraintLateralToNasal02() {
		Segment segment = factory.toSegment("l");

		Segment pNas = factory.toSegment("[+nas]");
		Segment nLat = factory.toSegment("[-lat]");

		assertMatch(segment, pNas, nLat);
	}

	@Test
	void testConstraintNasalToLateral01() {
		Segment segment = factory.toSegment("n");

		FeatureArray features = segment.getFeatures();
		features.set(5, 1);

		int expected = -1;
		int received = features.get(6);

		assertEquals(expected, received);
	}

	@Test
	void testConstraintNasalToLateral02() {
		Segment segment = factory.toSegment("n");

		Segment pLat = factory.toSegment("[+lat]"); // i = 5
		Segment nNas = factory.toSegment("[-nas]"); // i = 6

		assertMatch(segment, pLat, nNas);
	}

	@Test
	void testConstaintSonorant() {
		Segment segment = factory.toSegment("i");

		Segment nSon = factory.toSegment("[-son]"); // i = 1
		Segment pCon = factory.toSegment("[+con]"); // i = 0

		assertMatch(segment, nSon, pCon);
	}

	@Test
	void testConstraintConsonant() {
		Segment segment = factory.toSegment("s");

		Segment nCon = factory.toSegment("[-con]"); // i = 0
		Segment pSon = factory.toSegment("[+son]"); // i = 1

		assertMatch(segment, nCon, pSon);
	}

	@Test
	void testConstraintConsonantalRelease() {
		Segment segment = factory.toSegment("kx");

		Segment nCon = factory.toSegment("[-con]"); // i = 0
		Segment nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCon, nRel);
	}

	@Test
	void testConstraintContinuantRelease() {
		Segment segment = factory.toSegment("kx");

		Segment nCnt = factory.toSegment("[+cnt]"); // i = 0
		Segment nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintSonorantRelease() {
		Segment segment = factory.toSegment("ts");

		Segment nCnt = factory.toSegment("[+son]"); // i = 0
		Segment nRel = factory.toSegment("[-rel]"); // i = 1

		assertMatch(segment, nCnt, nRel);
	}

	@Test
	void testConstraintReleaseConsonantal() {
		Segment segment = factory.toSegment("r");

		Segment pRel = factory.toSegment("[+rel]"); // i = 0
		Segment pCon = factory.toSegment("[+con]"); // i = 1

		assertMatch(segment, pRel, pCon);
	}

	private static <T> void assertMatches(Segment x, Segment y) {
		assertTrue(x.matches(y));
	}

	private static <T> void assertNotMatches(Segment x, Segment y) {
		assertFalse(x.matches(y));
	}

	private static void assertMatch(
			Segment segment,
			Segment modifier,
			Segment matching
	) {
		Segment alter= new StandardSegment(segment);
		alter.alter(modifier);
		assertTrue(alter.matches(matching), () -> {
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
			return message;
		});
	}
}
