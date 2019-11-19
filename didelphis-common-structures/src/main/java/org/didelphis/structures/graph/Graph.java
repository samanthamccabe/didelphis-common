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

package org.didelphis.structures.graph;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.structures.maps.GeneralTwoKeyMultiMap;
import org.didelphis.structures.maps.interfaces.TwoKeyMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class {@code Graph}
 */
@ToString
@EqualsAndHashCode (callSuper = true)
public final class Graph<S>
		extends GeneralTwoKeyMultiMap<String, Arc<S>, String> {

	public Graph() {
		super(LinkedHashMap.class, ArrayList.class);
	}

	public Graph(@NonNull TwoKeyMap<String, Arc<S>, Collection<String>> graph) {
		super(LinkedHashMap.class, ArrayList.class, graph);
	}

	public Map<Arc<S>, Collection<String>> get(String key) {
		return getDelegate().get(key);
	}

	public static class EmptyArc<S> implements Arc<S> {
		@Override
		public String toString() {
			return "";
		}

		@Override
		public int match(S sequence, int index) {
			return 0;
		}
	}
}
