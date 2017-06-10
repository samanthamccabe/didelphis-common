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

package org.didelphis.common.utilities;

import java.util.regex.Pattern;

/**
 * Created by samantha on 2/24/17.
 */
public final class Patterns {
	
	private Patterns() {}
	
	public static Pattern template(String head, String... vars) {
		String regex = head;
		for (int i = 0; i < vars.length;i++) {
			regex = regex.replace("$"+(i+1), vars[i]);
		}
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}
	
	public static Pattern compile(String head, String... tail) {
		String regex = concat(head, tail);
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}

	private static String concat(String head, String... tail) {
		StringBuilder sb = new StringBuilder(0x100);
		sb.append(head);
		for (String string : tail) {
			sb.append(string);
		}
		return sb.toString();
	}
}
