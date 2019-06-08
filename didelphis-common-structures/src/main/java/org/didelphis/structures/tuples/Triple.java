/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.structures.tuples;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Iterator;

/**
 * Class {@code Triple}
 *
 * A value-class which should be used judiciously. It's main purpose is to
 * help provide views of keys-value in two-key maps, and of the arcs in a graph.
 *
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Triple} is used to provide an {@link Iterator} for two-key maps.
 *
 * @since 0.2.0
 */
@EqualsAndHashCode
public class Triple<T, U, V> {

	private final T element1;
	private final U element2;
	private final V element3;

	public Triple(
			@NonNull T element1, @NonNull U element2, @NonNull V element3
	) {
		this.element1 = element1;
		this.element2 = element2;
		this.element3 = element3;
	}

	@NonNull
	public T getFirstElement() {
		return element1;
	}

	@NonNull
	public U getSecondElement() {
		return element2;
	}

	@NonNull
	public V getThirdElement() {
		return element3;
	}

	@NonNull
	@Override
	public String toString() {
		return "<" + element1 + ", " + element2 + ", " + element3 + '>';
	}
}
