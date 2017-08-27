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

package org.didelphis.language.machines.simple;

import org.didelphis.language.machines.interfaces.MachineMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * Created by samantha on 3/3/17.
 */
public class SimpleMatcher implements MachineMatcher<String> {

	@Override
	public int match(@NotNull String target, @NotNull String arc, int index) {
		return 0;
	}
}
