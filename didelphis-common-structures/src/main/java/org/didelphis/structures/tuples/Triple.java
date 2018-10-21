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
 * @date 2017-02-11
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
