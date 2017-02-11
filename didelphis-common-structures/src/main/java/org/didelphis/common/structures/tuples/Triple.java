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
 * Samantha Fiona Morrigan McCabe
 * Created: 4/10/2016
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
