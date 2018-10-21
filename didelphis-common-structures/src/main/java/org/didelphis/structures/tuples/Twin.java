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
import org.didelphis.utilities.Templates;

import java.util.AbstractList;

/**
 * Class {@code Pair}
 *
 * @date 2017-07-22
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
