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

import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.didelphis.common.language.phonetic.segments.StandardSegment;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Segmenter provides functionality to split strings into an an array where
 * each element represents a series of characters grouped according to their
 * functional value as diacritical marks or combining marks.
 *
 * @author Samantha Fiona Morrigan McCabe
 */
public final class SegmenterUtil {

	private static final Logger LOG = LoggerFactory.getLogger(SegmenterUtil.class);

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
	@Deprecated
	public static <N extends Number> Segment<N> getSegment(
			String string, 
			FeatureMapping<N> mapping,
			Segmenter formatterMode) {
		return getSegment(string, mapping, null, formatterMode);
	}

	@Deprecated
	public static <N extends Number> Segment<N> getSegment(
			String string,
			FeatureMapping<N> featureMapping,
			Collection<String> reservedStrings,
			Segmenter formatterMode) {
			return featureMapping.getSegment(string);
	}

	@Deprecated
	public static List<String> getSegmentedString(String word, Iterable<String> keys, Segmenter formatterMode) {
		return formatterMode.split(word, keys);
	}

	public static <N extends Number> Sequence<N> getSequence(String word, FeatureMapping<N> featureMapping, Collection<String> reserved, Segmenter formatterMode) {
		Collection<String> keys = getKeys(featureMapping, reserved);
		List<String> list = formatterMode.split(word, keys);
		FeatureModel<N> featureModel = featureMapping.getFeatureModel();
		Sequence<N> sequence = new BasicSequence<>(featureModel);
		for (String string : list) {
			Segment<N> segment;
			if (reserved != null && reserved.contains(string)) {
				segment = new StandardSegment<>(string, new SparseFeatureArray<>(featureModel), featureModel);
			} else if (string.startsWith("[") && featureMapping.getSpecification().size() > 0) {
				segment = new StandardSegment<>(string, featureModel.parseFeatureString(string), featureModel);
			} else {
				segment = featureMapping.getSegment(string);
			}
			sequence.add(segment);
		}
		return sequence;
	}

	private static <N extends Number> Collection<String> getKeys(
			FeatureMapping<N> mapping,
			Collection<String> reserved) {
		Collection<String> keys = new ArrayList<>(mapping.getSymbols());
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
			LOG.warn("Unmatched " + left + " in " + string);
		}
		return endIndex;
	}
}
