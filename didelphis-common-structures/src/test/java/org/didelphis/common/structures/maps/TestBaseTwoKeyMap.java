package org.didelphis.common.structures.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Created by samantha on 1/16/17.
 */
public class TestBaseTwoKeyMap {
	public static <T, U, V> void testGet(TwoKeyMap<T, U, V> map, T k1, U k2, V v) {
		String m = "key (" + k1 + ',' + k2 + ") retrieved unexpected value";
		assertEquals(m, v, map.get(k1, k2));
	}

	public static <T, U, V> void testNullGet(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String m = "key (" + k1 + ',' + k2 + ") should not exist";
		assertNull(m, map.get(k1, k2));
	}

	public static <T, U, V> void testContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String m = "key (" + k1 + ',' + k2 + ") should be found in map";
		assertTrue(m, map.contains(k1, k2));
	}

	public static <T, U, V> void testNotContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String m = "key (" + k1 + ',' + k2 + ") should *not* be found in map";
		assertFalse(m, map.contains(k1, k2));
	}
}
