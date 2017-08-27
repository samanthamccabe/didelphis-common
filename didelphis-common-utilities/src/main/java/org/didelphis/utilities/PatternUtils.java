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

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Utility Class {@code PatternUtils}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-24
 * @since 0.1.0
 */
@UtilityClass
public class PatternUtils {

	private final Pattern WHITESPACES = Pattern.compile("\\s+");
	
	@NotNull
	public String cleanSpaces(@NotNull CharSequence charSequence) {
		return WHITESPACES.matcher(charSequence).replaceAll(" ");
	}
	
	@NotNull
	public Pattern template(@NotNull String head, @NotNull String... vars) {
		String regex = head;
		for (int i = 0; i < vars.length; i++) {
			regex = regex.replace("$" + (i + 1), vars[i]);
		}
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}

	@NotNull
	public Pattern compile(@NotNull String head, @NotNull String... tail) {
		String regex = concat(head, tail);
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}

	@NotNull
	private String concat(@NotNull String head, @NotNull String... tail) {
		StringBuilder sb = new StringBuilder(0x100);
		sb.append(head);
		for (String string : tail) {
			sb.append(string);
		}
		return sb.toString();
	}
}
