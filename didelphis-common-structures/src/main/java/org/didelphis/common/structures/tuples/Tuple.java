/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.structures.tuples;

import java.util.Objects;

/**
 * {@code Tuple} is a class which should be used judiciously. It's main purpose 
 * is to help provide views of key sets in two-key maps.
 *
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Tuple} is used to provide an {@code Iterator} for two-key maps.
 *
 *
 * Samantha Fiona Morrigan McCabe
 * Created: 4/10/2016
 */
public class Tuple<L, R> {

	private final L left;
	private final R right;

	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public Tuple(Tuple<L, R> tuple) {
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

	@Override
	public String toString() {
		return "<" + left + ", " + right + '>';
	}
}
