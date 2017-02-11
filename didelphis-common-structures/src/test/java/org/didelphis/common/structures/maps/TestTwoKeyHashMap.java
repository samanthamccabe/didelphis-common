package org.didelphis.common.structures.maps;

import org.junit.Test;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by samantha on 1/16/17.
 */
public class TestTwoKeyHashMap extends TestBaseTwoKeyMap {
	
	@Test
	public void testPutAndGet() {
		TwoKeyMap<String, String, String> map = new TwoKeyHashMap<>();
		map.put("a", "b", "c");
		
		testGet(map, "a", "b", "c");
		testNullGet(map, "b", "a");
		testNullGet(map, "b", "c");
	}

	@Test
	public void testContains() {
		TwoKeyMap<String, String, String> map = new TwoKeyHashMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		testContains(map, "a1", "b1");
		testContains(map, "a2", "b2");
		testContains(map, "a3", "b3");

		testNotContains(map, "b1", "a1");
		testNotContains(map, "b2", "a2");
		testNotContains(map, "b3", "a3");
	}

	@Test
	public void testKeys() {
		TwoKeyMap<String, String, String> map = new TwoKeyHashMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		Collection<Tuple<String, String>> expected = new HashSet<>();
		expected.add(new Tuple<>("a1", "b1"));
		expected.add(new Tuple<>("a2", "b2"));
		expected.add(new Tuple<>("a3", "b3"));

		assertEquals("Unexpected Key Set", expected, map.keys());
	}
}
