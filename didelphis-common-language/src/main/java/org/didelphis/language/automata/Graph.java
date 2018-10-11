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

package org.didelphis.language.automata;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.structures.maps.GeneralTwoKeyMultiMap;

import java.util.HashMap;

import static org.didelphis.structures.Suppliers.ofLinkedHashMap;
import static org.didelphis.structures.Suppliers.ofList;

/**
 * Class {@code Graph}
 *
 * @author Samantha Fiona McCabe
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public final class Graph<T> extends GeneralTwoKeyMultiMap<String, T, String> {

	public Graph() {
		super(new HashMap<>(), ofLinkedHashMap(), ofList());
	}
	
	public Graph(@NonNull GeneralTwoKeyMultiMap<String, T, String> graph) {
		super(graph, new HashMap<>(), ofLinkedHashMap(), ofList());
	}
}
