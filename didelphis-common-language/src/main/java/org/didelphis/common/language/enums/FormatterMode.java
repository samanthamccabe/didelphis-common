/******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.enums;

import org.didelphis.common.language.phonetic.Formatter;
import org.didelphis.common.language.phonetic.Segmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.didelphis.common.utilities.Split.*;

/**
 * This type is to succeed the earlier SegmentationMode and Normalizer mode
 * enums by merging their functionality. We originally supported types that
 * were entirely unnecessary and presented the user with an excess of options,
 * most of where were of no value (compatibility modes, or segmentation with
 * composition e.g.)
 * <p>
 * Samantha Fiona Morrigan McCabe
 * Created: 1/14/2015
 */
public enum FormatterMode implements Segmenter, Formatter {
	
	// No change to input strings
	NONE(null) {
		@Override
		public List<String> split(String string) {
			return split(string, Collections.emptyList());
		}

		@Override
		public List<String> split(String string, Iterable<String> special) {
			return splitToList(string, special);
		}
	},

	// Unicode Canonical Decomposition
	DECOMPOSITION(Normalizer.Form.NFD) {
		@Override
		public List<String> split(String string) {
			return split(string, Collections.emptyList());
		}

		@Override
		public List<String> split(String string, Iterable<String> special) {
			return splitToList(normalize(string), special);
		}
	},

	// Unicode Canonical Decomposition followed by Canonical Composition
	COMPOSITION(Normalizer.Form.NFC) {
		@Override
		public List<String> split(String string) {
			return split(string, Collections.emptyList());
		}

		@Override
		public List<String> split(String string, Iterable<String> special) {
			return splitToList(normalize(string), special);
		}
	},

	// Uses segmentation algorithm with Unicode Canonical Decomposition
	INTELLIGENT(Normalizer.Form.NFD) {

		private static final int BINDER_START       = 0x035C;
		private static final int BINDER_END         = 0x0362;
		private static final int SUPERSCRIPT_ZERO   = 0x2070;
		private static final int SUBSCRIPT_SMALL_T  = 0x209C;
		private static final int SUPERSCRIPT_TWO    = 0x00B2;
		private static final int SUPERSCRIPT_THREE  = 0x00B3;
		private static final int SUPERSCRIPT_ONE    = 0x00B9;

		private final Pattern pattern = Pattern.compile("(\\$[^$]*\\d+)");

		@Override
		public List<String> split(String string, Iterable<String> special) {
			String word = normalize(string);

			List<String> strings = new ArrayList<>();
			StringBuilder sb = new StringBuilder(4);
			for (int i = 0; i < word.length(); ) {
				// Get the word from current position on
				int index = parseParens(word, i);
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

		@Override
		public List<String> split(String string) {
			return split(string, Collections.emptyList());
		}

		// Finds longest item in keys which the provided string starts with
		// Also can be used to grab index symbols
		private String getBestMatch(String word, Iterable<String> keys) {

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
			return isSuperscriptAsciiDigit(ch) ||
				         isMathematicalSubOrSuper(ch) ||
				         isCombiningClass(ch);
		}

		private boolean isDoubleWidthBinder(char ch) {
			return BINDER_START <= ch && ch <= BINDER_END;
		}
		
		private boolean isSuperscriptAsciiDigit(char value) {
			return value == SUPERSCRIPT_TWO || 
				         value == SUPERSCRIPT_THREE ||
				         value == SUPERSCRIPT_ONE;
		}

		private boolean isMathematicalSubOrSuper(char value) {
			return SUPERSCRIPT_ZERO <= value && value <= SUBSCRIPT_SMALL_T;
		}

		private boolean isCombiningClass(char ch) {
			int type = Character.getType(ch);
			return type == Character.MODIFIER_LETTER ||
				         type == Character.MODIFIER_SYMBOL ||
				         type == Character.COMBINING_SPACING_MARK ||
				         type == Character.NON_SPACING_MARK;
		}
	};

	private final Normalizer.Form form;

	FormatterMode(Normalizer.Form param) {
		form = param;
	}

	@Override
	public String normalize(String string) {
		return (form == null) ? string : Normalizer.normalize(string, form);
	}

	private final transient Logger logger = LoggerFactory.getLogger(FormatterMode.class);
}