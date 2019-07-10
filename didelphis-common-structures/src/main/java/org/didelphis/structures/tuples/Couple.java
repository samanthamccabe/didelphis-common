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
import org.didelphis.utilities.Safe;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * {@code Tuple} is a class which should be used judiciously. It's main purpose
 * is to help provide views of key sets in two-key maps.
 * <p>
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Tuple} is used to provide an {@code Iterator} for two-key maps.
 *
 * @since 0.2.0
 */
@EqualsAndHashCode
public class Couple<L, R> implements Tuple<L, R> {

	@Nullable private final L left;
	@Nullable private final R right;

	public Couple(@Nullable L left, @Nullable R right) {
		this.left = left;
		this.right = right;
	}

	public Couple(@NonNull Tuple<? extends L, ? extends R> tuple) {
		left = tuple.getLeft();
		right = tuple.getRight();
	}

	@Nullable
	@Override
	public L getLeft() {
		return left;
	}

	@Nullable
	@Override
	public R getRight() {
		return right;
	}

	@Override
	public boolean contains(@Nullable Object entry) {
		return Objects.equals(entry, left) || Objects.equals(entry, right);
	}

	@NonNull
	@Override
	public String toString() {
		return "<" + Safe.toString(left) + ", " + Safe.toString(right) + '>';
	}
}
