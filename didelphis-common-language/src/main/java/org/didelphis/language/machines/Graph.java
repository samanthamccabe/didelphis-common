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

package org.didelphis.language.machines;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.structures.maps.GeneralTwoKeyMultiMap;

/**
 * Class {@code Graph}
 *
 * @author Samantha Fiona McCabe
 * @date 2016-01-28
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Graph<T> extends GeneralTwoKeyMultiMap<String, T, String> {

	public Graph(@NonNull GeneralTwoKeyMultiMap<String, T, String> graph) {
		super(graph);
	}
}
