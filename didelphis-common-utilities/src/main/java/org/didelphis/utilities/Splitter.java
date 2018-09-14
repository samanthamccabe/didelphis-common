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

package org.didelphis.utilities;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class {@code Splitter}
 *
 * General collection of String segmentation tools, including bracket matching
 * related tasks
 *
 * @since 0.2.0
 */
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Splitter {
	
	/**
	 * A simple utility method for splitting input at line breaks using the 
	 * regular expression {@code "\r?\n|\r")}
	 *
	 * @param string the input to be split; must not be null
	 *
	 * @return a list containing each line found in the original input; not null
	 */
	@NonNull
	public List<String> lines(@NonNull String string) {

		List<String> lines = new ArrayList<>();
		int index = 0;
		int cursor = 0; // 
		while (index < string.length()) {

			if (string.startsWith("\r\n", index)) {
				lines.add(string.substring(cursor, index));
				index += 2;
				cursor = index;
			} else if (string.startsWith("\r", index)) {
				lines.add(string.substring(cursor, index));
				index++;
				cursor = index;
			} else if (string.startsWith("\n", index)) {
				lines.add(string.substring(cursor, index));
				index++;
				cursor = index;
			} else {
				index++;
			}
		}

		lines.add(string.substring(cursor));
		return lines;
	}

	/**
	 * Splits inputs such that each character in the input becomes an element in
	 * the returned list of Strings unless: a substring is found in the
	 * parameter {@code special}, in which case it is preserved; or a chunk is
	 * delimited parenthetically, in which case the contents of the delimited
	 * chunk will also be preserved.
	 *
	 * @param string the string to be split
	 * @param delimiters a map of parenthetical delimiters whose contents will
	 * 		not be split
	 * @param special a list of special strings which will not be split. This
	 * 		may be null; if it is, the specials are ignored, giving the same
	 *
	 * @return a new list which, if joined with no delimiter, would return the
	 * 		original string; not {@code null}
	 */
	@NonNull
	public List<String> toList(
			@NonNull String string, 
			@NonNull Map<String, String> delimiters, 
			@Nullable Iterable<String> special
	) {
		List<String> strings = new ArrayList<>();
		for (int i = 0; i < string.length(); i++) {

			int index = parseParens(string, delimiters, i);
			if (index >= 0) {
				strings.add(string.substring(i, index));
				i = index - 1;
			} else {
				String substring = string.substring(i);
				String matchedSpecial = "";
				if (special != null) {
					for (String s : special) {
						if (substring.startsWith(s) && s.length() > matchedSpecial.length()) {
							matchedSpecial = s;
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
	 * Splits inputs on the basis of whitespace only, while also preserving
	 * parenthetically delimited chunks. Thus, a string:
	 * <p>
	 * {@code "a b (c d) e"}
	 * <p>
	 * will be split into four elements: 
	 * <ul>
	 *     <li>{@code "a"}</li>
	 *     <li>{@code "b"}</li>
	 *     <li>{@code "(cd)"}</li>
	 *     <li>{@code "e"}</li>
	 * </ul>
	 *
	 * @param string the input that is to be split; cannot be {@code null}
	 *
	 * @param delimiters
	 * @return a new list containing elements from the provided input; will not
	 * 		be {@code null}
	 */
	@NonNull
	public List<String> whitespace(
			@NonNull String string, 
			@NonNull Map<String, String> delimiters
	) {
		List<String> list = new ArrayList<>();
		int cursor = 0;
		for (int i = 0; i < string.length();) {
			if (isWhitespace(string.charAt(i))) {
				if (i != cursor) {
					String chunk = string.substring(cursor, i);
					list.add(chunk);
				}
				i++;
				cursor = i;
			} else {
				int end = parseParens(string, delimiters, i);
				i = (end > i) ? end : i + 1;
			}
		}
		
		if (cursor < string.length()) {
			list.add(string.substring(cursor));
		}
		
		return list;
	}
	
	private boolean isWhitespace(char c) {
		return " \t\n\f\r".indexOf(c) >= 0;
	}

	/**
	 * Finds the index of the bracket matching the one at {@param index} within
	 * the provided {@code String}.
	 *
	 * @param string the {@code CharSequence} to be matched for
	 * @param parens a map of start to end characters for parentheses
	 * @param index of the opening bracket.
	 *
	 * @return the index of the corresponding closing bracket, or -1 if one is
	 * 		not found
	 */
	public int parseParens(
			@NonNull String string, 
			@NonNull Map<String, String> parens,
			int index
	) {
		for (String key: parens.keySet()) {
			if (string.startsWith(key, index)) {
				return findClosingBracket(string, key, parens, index);
			}
		}
		return -1;
	}

	/**
	 * Determines the index of the closing bracket which corresponds to the
	 * opening bracket in {@param string}, located at {@param startIndex}
	 * 
	 * @param string the input to be examined for parentheses
	 * @param left the opening parenthesis
	 * @param delimiters a map of opening and closing delimiters
	 * @param startIndex the index in {@param string} where to start looking
	 *
	 * @return the index of the closing bracket, if it was found; otherwise, -1
	 */
	public int findClosingBracket(
			@NonNull String string,
			@NonNull String left, 
			@NonNull Map<String, String> delimiters, 
			int startIndex
	) {
		int endIndex = startIndex;

		Deque<String> stack = new LinkedList<>();
		for (int i = startIndex + left.length(); i < string.length(); i++) {
			String substring = string.substring(i);
			for (Map.Entry<String, String> entry : delimiters.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				
				if (substring.startsWith(key)) {
					stack.add(key);
				} else if (substring.startsWith(val)) {
					if (stack.isEmpty()) {
						endIndex = i;
						break;
					} else
					if (stack.peekLast().equals(key)) {
						stack.removeLast();
					}
				}
			}
		}
		/* 'endIndex' is the index of the closing bracket; we advance by 1 to
		 * because we always need the next index for a substring that includes
		 * the closing bracket, or we need to continue operations after the
		 * bracketed expression is closed.
		 * 
		 * However, if the start and end indices are the same, then the matching
		 * parenthesis has not been found, thus we return -1
		 */
		return startIndex == endIndex ? -1 : endIndex + 1;
	}
}
