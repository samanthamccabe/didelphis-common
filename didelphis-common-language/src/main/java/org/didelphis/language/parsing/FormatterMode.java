/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.parsing;

import lombok.NonNull;

import org.didelphis.language.automata.Regex;
import org.didelphis.language.automata.matching.Match;

import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.didelphis.utilities.Splitter.*;

/**
 * Enum {@code FormatterMode}
 *
 * This type is to succeed the earlier {@code SegmentationMode} and {@code
 * Normalizer} mode enums by merging their functionality. We originally
 * supported types that were entirely unnecessary and presented the user with an
 * excess of options, most of where were of no value (compatibility modes, or
 * segmentation with composition e.g.)
 *
 * @since 0.1.0
 */
public enum FormatterMode implements Segmenter, Formatter {

	// No change to input strings
	NONE(null) {
		@NonNull
		@Override
		public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				@NonNull Map<String, String> delimiters
		) {
			return toList(string, delimiters, special);
		}
	},

	// Unicode Canonical Decomposition
	DECOMPOSITION(Form.NFD) {
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

	// Unicode Canonical Decomposition followed by Canonical Composition
	COMPOSITION(Form.NFC) {
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
		private static final int SUPER_TWO    = 0x00B2;
		private static final int SUPER_THREE  = 0x00B3;
		private static final int SUPER_ONE    = 0x00B9;
		private static final int BINDER_START = 0x035C;
		private static final int BINDER_END   = 0x0362;
		private static final int SUPER_ZERO   = 0x2070;
		private static final int SUB_SMALL_T  = 0x209C;
		/*>-------------------------------------------------------------------*/

		private final Regex pattern = new Regex("\\$[^$]*\\d+");

		@NonNull
		@Override
		@SuppressWarnings ({"OverlyComplexMethod", "OverlyLongMethod"})
		public List<String> split(
				@NonNull String string,
				@NonNull Iterable<String> special,
				@NonNull Map<String, String> delimiters
		) {
			String word = normalize(string);
			List<String> strings = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (i < word.length()) {
				// Get the word from current position on
				int index = parseParens(word, delimiters, new HashSet<>(), i);
				if (index > 0) {
					if (sb.length() > 0) {
						strings.add(sb.toString());
					}
					String substring = word.substring(i, index);
					strings.add(substring);
					sb = new StringBuilder();
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
						if (isBinder(ch)) {
							sb.append(ch);
							if (i + 1 < word.length()) {
								sb.append(word.charAt(i + 1));
								i++;
							}
						} else if (isAttachable(ch)) {
							sb.append(ch);
						} else  {
							// Not a diacritic
							if (sb.length() > 0) {
								strings.add(sb.toString());
							}
							sb = new StringBuilder();
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

			Match<String> backReferenceMatcher = pattern.match(word);
			if (backReferenceMatcher.matches()) {
				String group = backReferenceMatcher.group(0);
				if (group != null) {
					bestMatch = group;
				}
			}
			return bestMatch;
		}

		private boolean isAttachable(char c) {
			return isSuperAscii(c) || isMathSubOrSuper(c) || isCombining(c);
		}

		private boolean isBinder(char c) {
			return BINDER_START <= c && c <= BINDER_END;
		}

		private boolean isSuperAscii(char c) {
			return c == SUPER_TWO || c == SUPER_THREE || c == SUPER_ONE;
		}

		private boolean isMathSubOrSuper(char c) {
			return SUPER_ZERO <= c && c <= SUB_SMALL_T;
		}

		private boolean isCombining(char c) {
			for (int[] range : COMBINING_RANGES) {
				if (range[0] <= c && c <= range [1]) {
					return true;
				}
			}
			for (int aChar : MISC_COMBINING_CHARS) {
				if (aChar == c) {
					return true;
				}
			}
			return false;
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

	private static final int[][] COMBINING_RANGES = {
			{ 0x02B0, 0x02FF }, // Spacing Modifier Letters
			{ 0x0300, 0x036F }, // Combining Diacritical Marks
			{ 0x1AB0, 0x1AFF }, // Combining Diacritical Marks Extended
			{ 0x1DC0, 0x1DFF }, // Combining Diacritical Marks Supplement
			{ 0x20D0, 0x20FF }, // Combining Diacritical Marks for Symbols
			{ 0xFE20, 0xFE2F }, // Combining Half Marks
			{ 0x1D2C, 0x1D6A }, // Phonetic extensions (subset)
			{ 0x1D9B, 0x1DBF }, // Phonetic Extensions Supplement (subset)
			{ 0x2070, 0x209C }, // Superscripts and Subscripts (unofficial)
			{ 0x2DE0, 0x2DFF }, // Cyrillic Extended (subset)
			{ 0xA700, 0xA721 }, // Tone Marks
			{ 0xFE20, 0xFE26 }, // Combining Half Marks
			{ 0x0483, 0x0489 }  // Combining Cyrillic marks
	};

	private static final int[] MISC_COMBINING_CHARS = {
			0x005E, 0x0060, 0x00A8, 0x00AF, 0x00B4, 0x00B8,

			0x0374, // GREEK NUMERAL SIGN
			0x0375, // GREEK LOWER NUMERAL SIGN
			0x037A, // GREEK YPOGEGRAMMENI
			0x0384, // GREEK TONOS
			0x0385, // GREEK DIALYTIKA TONOS

			0x1D78, // MODIFIER LETTER CYRILLIC EN

			// Greek Extended
			0x1FBD, 0x1FBF, 0x1FC0, 0x1FC1, 0x1FCD, 0x1FCE, 0x1FCF, 0x1FDD,
			0x1FDE, 0x1FDF, 0x1FED, 0x1FEE, 0x1FEF, 0x1FFD, 0x1FFE,

			0x2C7C, // LATIN SUBSCRIPT SMALL LETTER J
			0x2C7D, // MODIFIER LETTER CAPITAL V
			0x2E2F, // VERTICAL TILDE
			0xA7F8, // MODIFIER LETTER CAPITAL H WITH STROKE
			0xA7F9, // MODIFIER LETTER SMALL LIGATURE OE
	};
}
