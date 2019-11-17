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

/**
 * Interface {@code Delegating}
 * <p>
 * Indicates that a data structure delegates some functionality to an inner
 * collection object and guarantees the structure is available through the API.
 *
 * @param <T> the type of the delegate object.
 *
 * @since 0.2.0
 */
public interface Delegating<T> {

	/**
	 * Provides access to the delegate used by the implementing class
	 *
	 * @return the delegate object; this must not return null.
	 */
	@NonNull
	T getDelegate();
}
