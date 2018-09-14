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

package org.didelphis.language.parsing;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.didelphis.utilities.Splitter.parseParens;
import static org.didelphis.utilities.Splitter.toList;

/**
 * Enum {@code FormatterMode}
 *
 * This type is to succeed the earlier {@code SegmentationMode} and {@code 
 * Normalizer} mode enums by merging their functionality. We originally 
 * supported types that were entirely unnecessary and presented the user with an
 * excess of options, most of where were of no value (compatibility modes, or
 * segmentation with composition e.g.)
 *
 * @author Samantha Fiona McCabe
 * @date 2015-01-14
 * @since 0.1.0
 */
public enum FormatterMode implements Segmenter, Formatter {

	// No change to input strings
	NONE(null) {
		@NonNull
		@Override
		public List<String> split(@NonNull String string) {
			return split(string, Collections.emptyList(),
					Collections.emptyMap());
		}

		@NonNull
		@Override
		public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				Map<String, String> delimiters
		) {
			return toList(string, delimiters, special);
		}
	},

	// Unicode Canonical Decomposition
	DECOMPOSITION(Form.NFD) {
		@NonNull
		@Override
		public List<String> split(@NonNull String string) {
			return split(string, Collections.emptyList(),
					Collections.emptyMap());
		}

		@NonNull
		@Override
		public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				Map<String, String> delimiters
		) {
			return toList(normalize(string), delimiters, special);
		}
	},

	// Unicode Canonical Decomposition followed by Canonical Composition
	COMPOSITION(Form.NFC) {
		@NonNull
		@Override
		public List<String> split(@NonNull String string) {
			return split(string, Collections.emptyList(),
					Collections.emptyMap());
		}

		@NonNull
		@Override
		public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				@NonNull Map<String, String> delimiters
		) {
			return toList(normalize(string), delimiters, special);
		}
	},

	// Uses segmentation algorithm with Unicode Canonical Decomposition
	INTELLIGENT(Form.NFD) {

		/* ------------------------------------------------------------------<*/
		private static final int BINDER_START      = 0x035C;
		private static final int BINDER_END        = 0x0362;
		private static final int SUPERSCRIPT_ZERO  = 0x2070;
		private static final int SUBSCRIPT_SMALL_T = 0x209C;
		private static final int SUPERSCRIPT_TWO   = 0x00B2;
		private static final int SUPERSCRIPT_THREE = 0x00B3;
		private static final int SUPERSCRIPT_ONE   = 0x00B9;
		/*>-------------------------------------------------------------------*/

		private final Pattern pattern = Pattern.compile("(\\$[^$]*\\d+)");

		@NonNull @Override public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				@NonNull Map<String, String> delimiters
		) {
			String word = normalize(string);

			List<String> strings = new ArrayList<>();
			StringBuilder sb = new StringBuilder(4);
			for (int i = 0; i < word.length(); ) {
				// Get the word from current position on
				int index = parseParens(word, delimiters, i);
				if (index > 0) {
					if (sb.length() > 0) {
						strings.add(sb.toString());
					}
					String substring = word.substring(i, index);
					strings.add(substring);
					sb = new StringBuilder(4);
					i = index;
				} else {
					String substring = word.substring(i);
					// Find the longest string in keys which the substring 
					// starts
					String key = getBestMatch(substring, special);
					if (sb.length() == 0) {
						// Assume that the first sb must be a base-character
						// This doesn't universally work (pre-nasalized, 
						// pre-aspirated), but we don't support this in our 
						// model yet
						if (key.isEmpty()) {
							// No special error handling if word starts with
							// diacritic, but may be desirable
							sb.append(word.charAt(i));
						} else {
							sb.append(key);
							i += key.length() - 1;
						}
					} else {
						char ch = word.charAt(i);
						if (isAttachable(ch)) { // is it a standard diacritic?
							sb.append(ch);
						} else {
							// Not a diacritic
							if (sb.length() > 0) {
								strings.add(sb.toString());
							}
							sb = new StringBuilder(4);
							if (key.isEmpty()) {
								sb.append(ch);
							} else {
								sb.append(key);
								i += key.length() - 1;
							}
						}
					}
					i++;
				}
			}
			if (sb.length() > 0) {
				strings.add(sb.toString());
			}
			return strings;
		}

		@NonNull
		@Override
		public List<String> split(@NonNull String string) {
			return split(string, Collections.emptyList(),
					Collections.emptyMap());
		}

		// Finds longest item in keys which the provided string starts with
		// Also can be used to grab index symbols
		@NonNull
		private String getBestMatch(
				@NonNull String word, @NonNull Iterable<String> keys
		) {

			String bestMatch = "";
			for (String key : keys) {
				if (word.startsWith(key) && bestMatch.length() < key.length()) {
					bestMatch = key;
				}
			}

			Matcher backReferenceMatcher = pattern.matcher(word);
			if (backReferenceMatcher.lookingAt()) {
				bestMatch = backReferenceMatcher.group();
			}
			return bestMatch;
		}

		private boolean isAttachable(char ch) {
			return isSuperscriptAsciiDigit(ch)
					|| isMathematicalSubOrSuper(ch)
					|| isCombiningClass(ch);
		}

		private boolean isDoubleWidthBinder(char ch) {
			return BINDER_START <= ch && ch <= BINDER_END;
		}

		private boolean isSuperscriptAsciiDigit(char value) {
			return value == SUPERSCRIPT_TWO
					|| value == SUPERSCRIPT_THREE
					|| value == SUPERSCRIPT_ONE;
		}

		private boolean isMathematicalSubOrSuper(char value) {
			return SUPERSCRIPT_ZERO <= value && value <= SUBSCRIPT_SMALL_T;
		}

		private boolean isCombiningClass(char ch) {
			int type = Character.getType(ch);
			return type == Character.MODIFIER_LETTER
					|| type == Character.MODIFIER_SYMBOL
					|| type == Character.COMBINING_SPACING_MARK
					|| type == Character.NON_SPACING_MARK;
		}
	};

	private final Form form;

	FormatterMode(@Nullable Form param) {
		form = param;
	}

	@NonNull
	@Override
	public String normalize(@NonNull String string) {
		return (form == null) ? string : Normalizer.normalize(string, form);
	}
}
