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

package org.didelphis.language.matching;

/**
 * Interface {@code Match}
 *
 * {@link java.util.regex.MatchResult} but parameterized
 * 
 * @author Samantha Fiona McCabe
 * @date 10/21/17
 */
public interface Match<T> {
	
	int start();
	
	int start(int group);
	
	int end();
	
	int end(int group);
	
	T group(int group);
	
	int groupCount();
}
