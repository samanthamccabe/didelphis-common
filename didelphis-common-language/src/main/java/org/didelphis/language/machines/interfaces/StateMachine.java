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

package org.didelphis.language.machines.interfaces;

import org.didelphis.language.machines.Graph;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Samantha Fiona McCabe
 * @date 4/7/2015
 */
public interface StateMachine<T> {

	@NotNull MachineParser<T> getParser();

	@NotNull MachineMatcher<T> getMatcher();

	String getId();

	/**
	 * Returns a maps of {@code StateMachine} ids to its associated graph. This
	 * ensures accessibility for machines which contain multiple embedded state
	 * machines.
	 * @return {@code Map} from {@code StateMachine} id → {@code Graph}
	 */
	@NotNull
	Map<String, Graph<T>> getGraphs();
	
	/**
	 * Returns the indices
	 * @param startIndex
	 * @param target
	 * @return
	 */
	@NotNull
	Collection<Integer> getMatchIndices(int startIndex, @NotNull T target);
}
