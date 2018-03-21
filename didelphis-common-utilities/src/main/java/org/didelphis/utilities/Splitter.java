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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class {@code Splitter}
 *
 * General collection of String segmentation tools, including bracket matching
 * related tasks
 *
 * @date 2017-03-03
 */
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Splitter {

	Pattern NEWLINE = Pattern.compile("\r?\n|\r");
	Pattern WHITESPACE = Pattern.compile("\\s+");

	Map<String, String> DELIMITERS = new HashMap<>();
	
	static {
		DELIMITERS.put("[", "]");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");
	}
	
	/**
	 * A simple utility method for splitting input at line breaks using the 
	 * regular expression {@code "\r?\n|\r")}
	 *
	 * @param lines the input to be split; must not be null
	 *
	 * @return a list containing each line found in the original input; not null
	 */
	@NonNull
	public List<String> lines(@NonNull CharSequence lines) {
		return Arrays.asList(NEWLINE.split(lines));
	}

	/**
	 * Splits inputs such that each character in the input becomes an element
	 * in the returned list of {@link String}s unless: a substring is found in
	 * the parameter {@code special}, in which case it is preserved; or a chunk
	 * is delimited parenthetically, in which case the contents of the delimited
	 * chunk will also be preserved.
	 * 
	 * @param string
	 * @param special
	 *
	 * @return
	 */
	@NonNull
	public List<String> toList(
			@NonNull String string, @Nullable Iterable<String> special
	) {
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
	 * Splits inputs on the basis of whitespace only, while also preserving
	 * parenthetically delimited chunks. Thus, a string:
	 * 
	 * {@code "a b (c d) e"}
	 * 
	 * will be split into four elements: {@code "a"}, {@code "b"}, {@code 
	 * "(c d)"}, and {@code "e"}.
	 * 
	 * @param string
	 * @return
	 */
	@NonNull
	public List<String> whitespace(String string) {
		List<String> list = new ArrayList<>();
		int cursor = 0;
		for (int i = 0; i < string.length();) {
			String substring = string.substring(i);
			Matcher matcher = WHITESPACE.matcher(substring);
			if (matcher.lookingAt()) {
				String chunk = string.substring(cursor, i);
				list.add(chunk);
				int end = matcher.end();
				cursor = end + i;
				i = cursor;
			} else {
				int end = parseParens(string, i);
				if (end > i) {
					i = end;
				} else {
					i++;
				}
			}
		}
		
		if (cursor < string.length()) {
			list.add(string.substring(cursor, string.length()));
		}
		
		return list;
	}
	
	/**
	 * Finds the index of the bracket matching the one at {@code index} within
	 * the provided {@code String}.
	 *
	 * @param string the {@link CharSequence} to be matched for
	 * @param index of the opening bracket.
	 *
	 * @return the index of the corresponding closing bracket.
	 */
	public int parseParens(@NonNull String string, int index) {
		return parseParens(string, DELIMITERS, index);
	}
	
	public int parseParens(
			@NonNull String string, 
			@NonNull Map<String, String> parens,
			int index
	) {
		return parens.entrySet()
				.stream()
				.filter(entry -> string.startsWith(entry.getKey(), index))
				.findFirst()
				.map(entry -> findClosingBracket(string,
						entry.getKey(),
						entry.getValue(),
						index
				))
				.orElse(-1);
	}

	/**
	 *  Determines the index of the closing bracket which corresponds to the 
	 *  opening bracket in {@param string}, located at {@param startIndex}
	 *
	 * @param string
	 * @param startIndex
	 * @param left
	 * @param right
	 *
	 * @return
	 */
	public int findClosingBracket(
			@NonNull CharSequence string, int startIndex, char left, char right
	) {
		int count = 1;
		int endIndex = startIndex;
		for (int i = startIndex + 1; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch == right && count == 1) {
				endIndex = i;
				break;
			} else if (ch == right) {
				count++;
			} else if (ch == left) {
				count--;
			}
		}
		/* 'endIndex' is the index of the closing bracket; we advance by 1 to 
		 * because we always need the next index for a substring that includes
		 * the closing bracket, or we need to continue operations after the
		 * bracketed expression is closed. 
		 */
		return endIndex + 1;
	}

	/**
	 * Determines the index of the closing bracket which corresponds to the
	 * opening bracket in {@param string}, located at {@param startIndex}
	 * 
	 * @param string
	 * @param left
	 * @param right
	 * @param startIndex
	 *
	 * @return
	 */
	public int findClosingBracket(
			@NonNull String string, @NonNull String left, @NonNull String right, int startIndex
	) {
		int count = 1;
		int endIndex = startIndex;

		for (int i = startIndex + 1; i < string.length(); i++) {
			if (string.startsWith(right, i) && count == 1) {
				endIndex = i;
				break;
			} else if (string.startsWith(right, i)) {
				count++;
			} else if (string.startsWith(left, i)) {
				count--;
			}
		}
		/* 'endIndex' is the index of the closing bracket; we advance by 1 to
		 * because we always need the next index for a substring that includes
		 * the closing bracket, or we need to continue operations after the
		 * bracketed expression is closed.
		 */
		return endIndex + 1;
	}
}
