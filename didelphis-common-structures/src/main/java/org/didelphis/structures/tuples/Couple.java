/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.structures.tuples;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Objects;

/**
 * {@code Tuple} is a class which should be used judiciously. It's main purpose
 * is to help provide views of key sets in two-key maps.
 * <p>
 * In many contexts, use of a class like this might indicate poor design. As it
 * is, {@code Tuple} is used to provide an {@code Iterator} for two-key maps.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-07-22
 * @since 0.2.0
 */
@EqualsAndHashCode
public class Couple<L, R> implements Tuple<L, R> {

	@NonNull private final L left;
	@NonNull private final R right;

	public Couple(@NonNull L left, @NonNull R right) {
		this.left = left;
		this.right = right;
	}

	public Couple(@NonNull Tuple<L, R> tuple) {
		left = tuple.getLeft();
		right = tuple.getRight();
	}

	@NonNull
	@Override
	public L getLeft() {
		return left;
	}

	@NonNull
	@Override
	public R getRight() {
		return right;
	}

	@Override
	public boolean contains(@NonNull Object entry) {
		return Objects.equals(entry, left) || Objects.equals(entry, right);
	}

	@NonNull
	@Override
	public String toString() {
		return "<" + left + ", " + right + '>';
	}
}
