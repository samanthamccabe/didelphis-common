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
import org.didelphis.utilities.Templates;

import java.util.AbstractList;

/**
 * Class {@code Pair}
 *
 * @since 0.2.0
 */
@EqualsAndHashCode(callSuper = true)
public class Twin<E> extends AbstractList<E> implements Tuple<E, E> {

	@NonNull private final E left;
	@NonNull private final E right;

	public Twin(@NonNull E left, @NonNull E right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public String toString() {
		return "(" + left + ',' + right + ')';
	}

	@NonNull
	@Override
	public E get(int index) {
		if (index == 0) return left;
		if (index == 1) return right;
		String message = Templates.create()
				.add("Index: {} is too large")
				.with(index)
				.add("{} does not support indices other than 0 and 1")
				.with(Twin.class)
				.build();
		throw new IndexOutOfBoundsException(message);
	}

	@NonNull
	@Override
	public E getLeft() {
		return left;
	}

	@NonNull
	@Override
	public E getRight() {
		return right;
	}
}
