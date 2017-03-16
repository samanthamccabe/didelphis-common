package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.maps.interfaces.TwoKeyMultiMap;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samantha on 12/20/16.
 */
public class TestTwoKeyMultiHashMap extends TestBaseTwoKeyMap {

	@Test
	void testAdd() {
		TwoKeyMultiMap<String, String, String> map = new TwoKeyMultiHashMap<>();
		map.add("a", "b", "v1");
		map.add("a", "b", "v2");
		map.add("a", "b", "v3");

		Set<String> objects = new HashSet<>();
		objects.add("v1");
		objects.add("v2");
		objects.add("v3");
		
		testGet(map, "a", "b", objects);
		testNullGet(map, "b", "a");
		testNullGet(map, "b", "c");
	}

}
