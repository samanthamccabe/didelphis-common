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

package org.didelphis.structures.graph;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralTwoKeyMultiMap;

import java.util.HashMap;

/**
 * Class {@code Graph}
 *
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public final class Graph<S> extends GeneralTwoKeyMultiMap<String, Arc<S>, String> {

	public Graph() {
		super(new HashMap<>(), Suppliers.ofLinkedHashMap(), Suppliers.ofList());
	}
	
	public Graph(@NonNull GeneralTwoKeyMultiMap<String, Arc<S>, String> graph) {
		super(graph, new HashMap<>(), Suppliers.ofLinkedHashMap(), Suppliers.ofList());
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
