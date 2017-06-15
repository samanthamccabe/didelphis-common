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

package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 11/23/2014
 */
public class SequenceFactory<T> {

	private static final Logger LOG = LoggerFactory.getLogger(SequenceFactory.class);
	
	private static final Pattern BACKREFERENCE_PATTERN = Pattern.compile("(\\$[^$]*\\d+)");

	private final FeatureMapping<T> featureMapping;
	private final VariableStore variableStore;
	private final FormatterMode formatterMode;
	private final Set<String>   reservedStrings;

	private final Segment<T> dotSegment;
	private final Segment<T> borderSegment;

	private final Sequence<T> dotSequence;
	private final Sequence<T> borderSequence;
	
	public SequenceFactory(FeatureMapping<T> mapping, FormatterMode mode) {
		this(mapping, new VariableStore(mode), new HashSet<>(), mode);
	}

	public SequenceFactory(FeatureMapping<T> mapping, VariableStore store, Set<String> reserved, FormatterMode mode) {
		featureMapping = mapping;
		variableStore   = store;
		reservedStrings = reserved;
		formatterMode   = mode;

		FeatureModel<T> model = featureMapping.getFeatureModel();
		FeatureArray<T> sparseArray = new SparseFeatureArray<>(model);

		//TODO: make these immutable
		dotSegment = new StandardSegment<>(".", sparseArray, model);
		borderSegment = new StandardSegment<>("#", sparseArray, model);

		dotSequence    = new BasicSequence<>(dotSegment);
		borderSequence = new BasicSequence<>(borderSegment);
	}

	public FormatterMode getFormatterMode() {
		return formatterMode;
	}

	public void reserve(String string) {
		reservedStrings.add(string);
	}

	public Segment<T> getDotSegment() {
		return dotSegment;
	}

	public Segment<T> getBorderSegment() {
		return borderSegment;
	}

	public Sequence<T> getDotSequence() {
		return dotSequence;
	}

	public Sequence<T> getBorderSequence() {
		return borderSequence;
	}

	public Segment<T> getSegment(String string) {
		if (!featureMapping.containsKey("#") && string.equals("#")) {
			return borderSegment;
		} else if (string.equals(".")) {
			return dotSegment;
		} else {
			return featureMapping.parseSegment(string);
		}
	}

	public Lexicon<T> getLexiconFromSingleColumn(Iterable<String> list) {
		Lexicon<T> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<T> sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon<T> getLexiconFromSingleColumn(String... list) {
		Lexicon<T> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<T> sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon<T> getLexicon(Iterable<List<String>> lists) {
		Lexicon<T> lexicon = new Lexicon<>();

		for (Iterable<String> row : lists) {
			List<Sequence<T>> lexRow = new ArrayList<>();
			for (String entry : row) {
				Sequence<T> sequence = getSequence(entry);
				lexRow.add(sequence);
			}
			lexicon.add(lexRow);
		}
		return lexicon;
	}

	public Sequence<T> getNewSequence() {
		return getSequence("");
	}

	public Sequence<T> getSequence(String word) {
		if (word.equals("#")) {
			return borderSequence;
		} else if (word.equals(".")) {
			return dotSequence;
		} else {
			Collection<String> keys = new ArrayList<>();
			keys.addAll(variableStore.getKeys());
			keys.addAll(reservedStrings);
			
			return SegmenterUtil.getSequence(word, featureMapping, keys, formatterMode);
		}
	}

	public boolean hasVariable(String label) {
		return variableStore.contains(label);
	}

	public List<Sequence<T>> getVariableValues(String label) {
		return variableStore.get(label).stream()
				.map(this::getSequence)
				.collect(Collectors.toList());
	}

	public List<String> getSegmentedString(String string) {
		return SegmenterUtil.getSegmentedString(string, getSpecialStrings(), formatterMode);
	}

	public FeatureMapping<T> getFeatureMapping() {
		return featureMapping;
	}

	public VariableStore getVariableStore() {
		return variableStore;
	}

	public String getBestMatch(String tail) {

		Iterable<String> keys = getSpecialStrings();
		String bestMatch = "";
		for (String key : keys) {
			if (tail.startsWith(key) && bestMatch.length() < key.length()) {
				bestMatch = key;
			}
		}
		Matcher backReferenceMatcher = BACKREFERENCE_PATTERN.matcher(tail);
		if (backReferenceMatcher.lookingAt()) {
			bestMatch = backReferenceMatcher.group();
		}
		return bestMatch;
	}

	@Override
	public String toString() {
		return "SequenceFactory{" +
		       ", featureMapping=" + featureMapping +
		       ", variableStore=" + variableStore +
		       ", formatterMode=" + formatterMode +
		       ", reservedStrings=" + reservedStrings +
		       '}';
	}

	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(variableStore.getKeys());
//		keys.addAll(featureMapping); // TODO: 
		keys.addAll(reservedStrings);
		return keys;
	}
}
