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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * {@code Tuple} is a class which should be used judiciously. It's main purpose 
 * is to help provide views of key sets in two-key maps.
 *
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Tuple} is used to provide an {@code Iterator} for two-key maps.
 *
 *
 * @author Samantha Fiona McCabe
 * Date: 4/10/2016
 */
public class Tuple<L, R> {

	private final L left;
	private final R right;

	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public Tuple(@NotNull Tuple<L, R> tuple) {
		left = tuple.left;
		right = tuple.right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}
	
	public boolean contains(Object entry) {
		return Objects.equals(entry, left) || Objects.equals(entry, right);
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple)) return false;
		Tuple<?, ?> tuple = (Tuple<?, ?>) o;
		return Objects.equals(left, tuple.left) &&
				Objects.equals(right, tuple.right);
	}

	@NotNull
	@Override
	public String toString() {
		return "<" + left + ", " + right + '>';
	}
}
