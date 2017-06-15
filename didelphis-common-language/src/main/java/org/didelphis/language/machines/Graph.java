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

package org.didelphis.language.machines;

import org.didelphis.structures.contracts.Delegating;
import org.didelphis.structures.maps.GeneralTwoKeyMultiMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/28/2016
 */
public class Graph<T> extends GeneralTwoKeyMultiMap<String, T, String> {
	
	private static final int HASH_ID = 0xa857a183;
	
	public Graph() {
	}

	public Graph(Delegating<Map<String, Map<T, Collection<String>>>> graph) {
		super(graph, HashSet.class);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Graph)) return false;
		Graph<?> graph = (Graph<?>) o;
		return super.equals(graph);
	}

	@Override
	public int hashCode() {
		return ~(HASH_ID * super.hashCode() >> 2);
	}

	@Override
	public String toString() {
		return "Graph{" + super.toString() + '}';
	}
}
