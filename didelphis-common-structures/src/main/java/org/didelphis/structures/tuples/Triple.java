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

package org.didelphis.structures.tuples;

import java.util.Objects;

/**
 * Triple is a class which should be used judiciously. It's main purpose is to
 * help provide views of keys-value in two-key maps, and of the arcs in a graph.
 * 
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Triple} is used to provide an {@code Iterator} for two-key maps.
 * 
 * @author Samantha Fiona McCabe
 * Date: 4/10/2016
 */
public class Triple<T, U, V> {

	private final T element1;
	private final U element2;
	private final V element3;

	public Triple(T element1, U element2, V element3) {
		this.element1 = element1;
		this.element2 = element2;
		this.element3 = element3;
	}

	public T getFirstElement() {
		return element1;
	}

	public U getSecondElement() {
		return element2;
	}

	public V getThirdElement() {
		return element3;
	}

	@Override
	public int hashCode() {
		return Objects.hash(element1, element2, element3);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Triple)) return false;
		Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
		return Objects.equals(element1, triple.element1) &&
				Objects.equals(element2, triple.element2) &&
				Objects.equals(element3, triple.element3);
	}

	@Override
	public String toString() {
		return "<" + element1 +
				", " + element2 +
				", " + element3 +
				'>';
	}
}
