/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.language.machines;

import org.didelphis.language.machines.interfaces.MachineMatcher;
import org.didelphis.language.machines.interfaces.MachineParser;
import org.didelphis.language.machines.interfaces.StateMachine;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Class {@code EmptyStateMachine}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-17
 */
@SuppressWarnings("unchecked")
public final class EmptyStateMachine<T> implements StateMachine<T>{

	private static final EmptyStateMachine<?> MACHINE = new EmptyStateMachine<>();
	private static final Map<String, ? extends Graph<?>> EMPTY_MAP = Collections.emptyMap();

	@NotNull
	public static <T> EmptyStateMachine<T> getInstance() {
		return (EmptyStateMachine<T>) MACHINE;
	}

	private EmptyStateMachine(){}

	@NotNull
	@Override
	public MachineParser<T> getParser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MachineMatcher<T> getMatcher() {
		return (t1, t2, i) -> i;
	}

	@Override
	public String getId() {
		return "Empty State Machine";
	}

	@NotNull
	@Override
	public Map<String, Graph<T>> getGraphs() {
		return (Map<String, Graph<T>>) EMPTY_MAP;
	}

	@Override
	public Collection<Integer> getMatchIndices(int startIndex, T target) {
		return Collections.singleton(startIndex);
	}
}
