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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Split (Utility) - a class for general string segmentation tools, bracket
 * matching, and related tasks
 * Created by samantha on 3/3/17.
 */
public final class Split {

	private static final Pattern NEWLINE = Pattern.compile("\r?\n|\r");

	private Split(){}

	public static List<String> splitLines(CharSequence lines) {
		return Arrays.asList(NEWLINE.split(lines));
	}

	@NotNull
	public static List<String> splitToList(
			@NotNull String string, @Nullable Iterable<String> special) {
		List<String> strings = new ArrayList<>();
		for (int i = 0; i < string.length(); i++) {

			int index = parseParens(string, i);
			if (index >= 0) {
				strings.add(string.substring(i, index));
				i = index - 1;
			} else {
				String substring = string.substring(i);
				String matchedSpecial = "";
				if (special != null) {
					for (String s : special) {
						if (substring.startsWith(s)) {
							matchedSpecial = s;
							break;
						}
					}
				}

				if (matchedSpecial.isEmpty()) {
					strings.add(string.substring(i, i + 1));
				} else {
					strings.add(matchedSpecial);
					i += matchedSpecial.length() - 1;
				}
			}
		}
		return strings;
	}

	/**
	 * Finds the closing bracket which accompanies
	 * @param string
	 * @param index
	 * @return
	 */
	public static int parseParens(@NotNull CharSequence string, int index) {
		switch (string.charAt(index)) {
			case '[':
				return findClosingBracket(string, '[', ']', index);
			case '(':
				return findClosingBracket(string, '(', ')', index);
			case '{':
				return findClosingBracket(string, '{', '}', index);
			default:
				return -1;
		}
	}

	/**
	 * 
	 * @param string
	 * @param left
	 * @param right
	 * @param startIndex
	 * @return
	 */
	public static int findClosingBracket(@NotNull CharSequence string, char left,
			char right, int startIndex) {
		int count = 1;
		int endIndex = startIndex;

		boolean matched = false;
		for (int i = startIndex + 1; i < string.length() && !matched; i++) {
			char ch = string.charAt(i);
			if (ch == right && count == 1) {
				matched = true;
				endIndex = i;
			} else if (ch == right) {
				count++;
			} else if (ch == left) {
				count--;
			}
		}
		return endIndex + 1;
	}
}
