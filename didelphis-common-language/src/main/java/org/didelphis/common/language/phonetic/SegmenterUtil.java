/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.FeatureModel;
import org.didelphis.common.language.phonetic.model.FeatureSpecification;
import org.didelphis.common.language.phonetic.model.StandardFeatureModel;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Segmenter provides functionality to split strings into an an array where
 * each element represents a series of characters grouped according to their
 * functional value as diacritical marks or combining marks.
 *
 * @author Samantha Fiona Morrigan McCabe
 */
public final class SegmenterUtil {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(SegmenterUtil.class);

	// Prevent the class from being instantiated
	private SegmenterUtil() {}

	/*
	public static List<Expression> getExpressions(String string, Collection<String> keys, FormatterMode formatterMode) {

		List<String> strings = getSegmentedString(string, keys, formatterMode);

		List<Expression> list = new ArrayList<Expression>();
		if (!strings.isEmpty()) {

			Expression buffer = new Expression();
			for (String symbol : strings) {
				if (symbol.equals("*") || symbol.equals("?") || symbol.equals("+")) {
					buffer.setMetacharacter(symbol);
					buffer = updateBuffer(list, buffer);
				} else if (symbol.equals("!")) {
					// first in an expression
					buffer = updateBuffer(list, buffer);
					buffer.setNegative(true);
				} else {
					buffer = updateBuffer(list, buffer);
					buffer.setExpression(symbol);
				}
			}
			if (!buffer.getExpression().isEmpty()) {
				list.add(buffer);
			}
		}
		return list;
	}

	private static Expression updateBuffer(Collection<Expression> list, Expression buffer) {
		// Add the contents of buffer if not empty
		if (!buffer.isEmpty()) {
			list.add(buffer);
			return new Expression();
		} else {
			return buffer;
		}
	}
*/
	public static Segment getSegment(String string, FeatureModel model,
	                                 Segmenter formatterMode) {
		return getSegment(string, model, new HashSet<>(), formatterMode);
	}

	@Deprecated
	public static Segment getSegment(String string,
	                                 FeatureModel model,
	                                 Collection<String> reservedStrings,
	                                 Segmenter formatterMode) {
//		Collection<String> keys = getKeys(model, reservedStrings);
//		List<String> strings = formatterMode.split(string, keys);

		FeatureArray<Double> array;
//		if (!strings.isEmpty()) {
		if (!string.isEmpty()) {
//			if (string.startsWith("[")) {
//				model.getSegment(string);
//			} else {
//			String bestMatch = "";
//			for (String symbol : model.getSymbols()) {
//				if (string.startsWith(symbol) && bestMatch.length() < string.length()) {
//					bestMatch = symbol;
//				}
//			}
//				array = model.getValue(bestMatch);
//			}
			return model.getSegment(string);
		}
		array = new SparseFeatureArray<>(model.getSpecification());
		return new Segment(string, array, model.getSpecification());
	}

	@Deprecated
	public static List<String> getSegmentedString(String word,
	                                              Iterable<String> keys,
	                                              Segmenter formatterMode
	) {
//		String normalString = formatterMode.normalize(word);
//		List<Symbol> segmentedSymbol = getCompositeSymbols(normalString, keys, formatterMode);
//		List<String> list = new ArrayList<>();
//		for (Symbol symbol : segmentedSymbol) {
//			StringBuilder head = new StringBuilder(symbol.getHead());
//			for (String s : symbol.getTail()) {
//				head.append(s);
//			}
//			list.add(head.toString());
//		}
		return formatterMode.split(word, keys);
	}

	public static Sequence getSequence(String word, FeatureModel model, 
	                                   Collection<String> reserved,
	                                   Segmenter formatterMode) {
		Collection<String> keys = getKeys(model, reserved);
		List<String> list = formatterMode.split(word, keys);
		FeatureSpecification specification = model.getSpecification();
		Sequence sequence = new BasicSequence(specification);
		for (String string : list) {
			Segment segment;
			if (string.startsWith("[") && !keys.contains(string)
					&& !Objects.equals(model, StandardFeatureModel.EMPTY_MODEL)) {
				segment = specification.getSegmentFromFeatures(string);
			} else {
				segment = model.getSegment(string);
			}
			sequence.add(segment);
		}
		return sequence;
	}

	private static Collection<String> getKeys(FeatureModel model, Collection<String> reserved) {
		Collection<String> keys = new ArrayList<>(model.getSymbols());
		if (reserved != null) {
			keys.addAll(reserved);
		}
		return keys;
	}

	private static List<String> separateBrackets(String word) {
		List<String> list = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < word.length(); ) {
			char c = word.charAt(i);
			if (c == '{') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer = new StringBuilder();
				}
				int index = getIndex(word, '{', '}', i) + 1;
				String substring = word.substring(i, index);
				list.add(substring);
				i = index;
			} else if (c == '(') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer = new StringBuilder();
				}
				int index = getIndex(word, '(', ')', i) + 1;
				String substring = word.substring(i, index);
				list.add(substring);
				i = index;
			} else if (c == '[') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer = new StringBuilder();
				}
				int index = getIndex(word, '[', ']', i) + 1;
				String substring = word.substring(i, index);
				list.add(substring);
				i = index;
			} else {
				buffer.append(c);
				i++;
			}
		}
		if (buffer.length() != 0) {
			list.add(buffer.toString());
		}
		return list;
	}
	
	private static int getIndex(CharSequence string, char left, char right, int startIndex) {
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
		if (!matched) {
			LOGGER.warn("Unmatched " + left + " in " + string);
		}
		return endIndex;
	}
}
