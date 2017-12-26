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

package org.didelphis.structures.contracts;

import lombok.NonNull;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface {@code Streamable}
 *
 * Like {@link Iterable} but for providing {@link Stream} access without the
 * need for a helper. As its only methods have default implementations, it is a
 * very simple add-on for non-collection classes which implement {@link
 * Iterable}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-07-28
 * @since 0.2.0
 */
@FunctionalInterface
public interface Streamable<E> extends Iterable<E> {

	@NonNull
	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@NonNull
	default Stream<E> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
}
