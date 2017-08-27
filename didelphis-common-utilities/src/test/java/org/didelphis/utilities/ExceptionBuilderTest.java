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

package org.didelphis.utilities;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionBuilderTest {

	@Test
	void testThrows() {
		Class<? extends Exception> type = UnsupportedOperationException.class;
/*-<*/  assertThrows(type, () -> Exceptions.create(type)
				.add("This is a test of {}")
				.with(type)
				.throwException());
/*>-*/
	}

	@Test
	void testBuild() {
		Exception exception = Exceptions.create(Exception.class)
				.add("This is a test of {} with some data.")
				.with(Exception.class.getCanonicalName())
				.data(Arrays.toString(Exception.class.getDeclaredFields()))
				.build();

		String message = exception.getMessage();
		assertFalse(message.isEmpty());
	}
}
