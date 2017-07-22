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

import java.util.AbstractList;
import java.util.Objects;

/**
 * Class {@code Pair}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 * Date: 2017-07-22
 */
public class Twin<E> extends AbstractList<E> implements Tuple<E, E> {

	private final E left;
	private final E right;

	public Twin(E left, E right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public E get(int index) {
		if (index == 0) {
			return left;
		} else if (index == 1) {
			return right;
		}
		throw new IndexOutOfBoundsException("Index: " + index + " is too large "
				+ Twin.class + " does not support indices other than 0 and 1.");
	}

	@Override
	public E getLeft() {
		return left;
	}

	@Override
	public E getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "(" + left + ',' + right + ')';
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Twin)) return false;
		final Twin other = (Twin) obj;
		return Objects.equals(this.left, other.left) &&
				Objects.equals(this.right, other.right);
	}
}
