package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.maps.interfaces.TwoKeyMap;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * Created by samantha on 1/16/17.
 */
public abstract class TwoKeyMapTestBase {
	
	protected TwoKeyMapTestBase() {}
	
	static <T, U, V> void testGet(TwoKeyMap<T, U, V> map, T k1, U k2, V v) {
		String string = "key (" + k1 + ',' + k2 + ") retrieved unexpected value";
		Assert.assertThat(string, map.get(k1, k2), is(v));
	}

	static <T, U, V> void testNullGet(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should not exist";
		Assert.assertThat(string, map.get(k1, k2), nullValue());
	}

	static <T, U, V> void testContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should be found in map";
		Assert.assertThat(string, map.contains(k1, k2), is(true));
	}

	static <T, U, V> void testNotContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should *not* be found in map";
		Assert.assertThat(string, map.contains(k1, k2), is(false));
	}
}
