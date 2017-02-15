package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by samantha on 2/14/17.
 */
public class VariableStoreTest {

	@Test
	public void testVariableComplex01() {
		VariableStore vs = new VariableStore(FormatterMode.NONE);
		vs.add("C  = p t k");
		vs.add("HC = hC");

		String expected =
				"C = p t k\n" + "HC = hp ht hk";
		assertEquals(expected, vs.toString());
	}

	@Test
	public void testVariableComplex02() {
		VariableStore vs = new VariableStore(FormatterMode.NONE);
		vs.add("C  = p t ");
		vs.add("C2 = CC");

		String expected =
				"C = p t\n" +
						"C2 = pp pt tp tt";
		assertEquals(expected, vs.toString());
	}

	@Test
	public void testVariableExpansion01() {
		VariableStore vs = new VariableStore(FormatterMode.NONE);

		vs.add("R = r l");
		vs.add("C = p t k R");

		String expected =
				"R = r l\n" +
						"C = p t k r l";
		assertEquals(expected, vs.toString());
	}

	@Test
	public void testVariableExpansion02()  {
		VariableStore vs = new VariableStore(FormatterMode.NONE);

		vs.add("N = n m");
		vs.add("R = r l");
		vs.add("L = R w y");
		vs.add("C = p t k L N");

		String expected = "" +
				"N = n m\n" +
				"R = r l\n" +
				"L = r l w y\n" +
				"C = p t k r l w y n m";
		assertEquals(expected, vs.toString());
	}

	@Test
	public void testVariableExpansion03() {
		VariableStore vs = new VariableStore(FormatterMode.INTELLIGENT);

		vs.add("C = p t k");
		vs.add("H = x ɣ");
		vs.add("CH = pʰ tʰ kʰ");
		vs.add("[CONS] = CH C H");

		String expected = "" +
				"C = p t k\n" +
				"H = x ɣ\n" +
				"CH = pʰ tʰ kʰ\n" +
				"[CONS] = pʰ tʰ kʰ p t k x ɣ";
		assertEquals(expected, vs.toString());
	}
}
