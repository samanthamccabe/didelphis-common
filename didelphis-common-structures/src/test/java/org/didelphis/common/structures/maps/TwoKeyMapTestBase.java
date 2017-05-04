package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.maps.interfaces.TwoKeyMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by samantha on 1/16/17.
 */
public abstract class TwoKeyMapTestBase {
	
	protected TwoKeyMapTestBase() {}
	
	static <T, U, V> void testGet(TwoKeyMap<T, U, V> map, T k1, U k2, V v) {
		String string = "key (" + k1 + ',' + k2 + ") retrieved unexpected value";
		assertEquals(v, map.get(k1, k2), string);
	}

	static <T, U, V> void testNullGet(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should not exist";
		assertNull(map.get(k1, k2), string);
	}

	static <T, U, V> void testContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should be found in map";
		assertTrue(map.contains(k1, k2),string);
	}

	static <T, U, V> void testNotContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should *not* be found in map";
		assertFalse(map.contains(k1, k2),string);
	}
}
