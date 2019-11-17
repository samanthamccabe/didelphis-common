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

package org.didelphis.structures.contracts;

import lombok.NonNull;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface {@code Streamable}
 * <p>
 * Like {@link Iterable} but for providing {@link Stream} access without the
 * need for a helper. As its only methods have default implementations, it is a
 * very simple add-on for non-collection classes which implement {@link
 * Iterable}
 *
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
