/**
 * ****************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.features.StandardFeatureArray;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 11/23/2014
 */
public class SequenceFactory {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(SequenceFactory.class);

	private static final SequenceFactory EMPTY_FACTORY  = new SequenceFactory();

	private static final Pattern BACKREFERENCE_PATTERN = Pattern.compile("(\\$[^\\$]*\\d+)");

	private final FeatureModel featureModel;
	private final VariableStore variableStore;
	private final FormatterMode formatterMode;
	private final Set<String>   reservedStrings;

	private final Segment  dotSegment;
	private final Segment  borderSegment;

	private final BasicSequence dotSequence;
	private final BasicSequence borderSequence;

	private SequenceFactory() {
		this(StandardFeatureModel.EMPTY_MODEL, new VariableStore(), new HashSet<>(), FormatterMode.NONE);
	}

	public SequenceFactory(FormatterMode modeParam) {
		this(StandardFeatureModel.EMPTY_MODEL, new VariableStore(), new HashSet<>(), modeParam);
	}

	public SequenceFactory(FeatureModel modelParam, FormatterMode modeParam) {
		this(modelParam, new VariableStore(), new HashSet<>(), modeParam);
	}

	public SequenceFactory(FeatureModel model, VariableStore store, Set<String> reserved, FormatterMode mode) {
		featureModel    = model;
		variableStore   = store;
		reservedStrings = reserved;
		formatterMode   = mode;

		FeatureSpecification specification = featureModel.getSpecification();
		FeatureArray<Double> sparseArray = new SparseFeatureArray<>(specification);
		FeatureArray<Double> standardArray = new StandardFeatureArray<>(FeatureSpecification.UNDEFINED_VALUE, specification);

		dotSegment = new Segment(".", sparseArray, specification);
		borderSegment = defineBorderSegment(specification, standardArray);

		dotSequence    = new BasicSequence(dotSegment);
		borderSequence = new BasicSequence(borderSegment);
	}

	public FormatterMode getFormatterMode() {
		return formatterMode;
	}

	private Segment defineBorderSegment(FeatureSpecification specification, FeatureArray<Double> standardArray) {
		return featureModel.containsKey("#") ? featureModel.getSegment("#") : new Segment("#", standardArray, specification);
	}

	public static SequenceFactory getEmptyFactory() {
		return EMPTY_FACTORY;
	}

	public void reserve(String string) {
		reservedStrings.add(string);
	}

	public Segment getDotSegment() {
		return dotSegment;
	}

	public Segment getBorderSegment() {
		return borderSegment;
	}

	public BasicSequence getDotSequence() {
		return dotSequence;
	}

	public BasicSequence getBorderSequence() {
		return borderSequence;
	}

	public Segment getSegment(String string) {
		if (!featureModel.containsKey("#") && string.equals("#")) {
			return borderSegment;
		} else if (string.equals(".")) {
			return dotSegment;
		} else {
			return SegmenterUtil.getSegment(string, featureModel, reservedStrings, formatterMode);
		}
	}

	public Lexicon getLexiconFromSingleColumn(Iterable<String> list) {
		Lexicon lexicon = new Lexicon();
		for (String entry : list) {
			Sequence sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon getLexiconFromSingleColumn(String... list) {
		Lexicon lexicon = new Lexicon();
		for (String entry : list) {
			Sequence sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon getLexicon(Iterable<List<String>> lists) {
		Lexicon lexicon = new Lexicon();

		for (List<String> row : lists) {
			List<Sequence> lexRow = new ArrayList<>();
			for (String entry : row) {
				Sequence sequence = getSequence(entry);
				lexRow.add(sequence);
			}
			lexicon.add(lexRow);
		}
		return lexicon;
	}

	public Sequence getNewSequence() {
		return getSequence("");
	}

	public Sequence getSequence(String word) {
		if (word.equals("#")) {
			Sequence sequence = new BasicSequence(featureModel.getSpecification());
			sequence.add(borderSegment);
			return sequence;
		} else if (word.equals(".")) {
			Sequence sequence = new BasicSequence(featureModel.getSpecification());
			sequence.add(dotSegment);
			return sequence;
		} else {
			Collection<String> keys = new ArrayList<>();
			keys.addAll(variableStore.getKeys());
			keys.addAll(reservedStrings);
			return SegmenterUtil.getSequence(word, featureModel, keys, formatterMode);
		}
	}

	public boolean hasVariable(String label) {
		return variableStore.contains(label);
	}

	public List<Sequence> getVariableValues(String label) {
		List<String> strings = variableStore.get(label);
		List<Sequence> sequences = new ArrayList<>();
		for (String string : strings) {
			sequences.add(getSequence(string));
		}
		return sequences;
	}

	public List<String> getSegmentedString(String string) {
		return SegmenterUtil.getSegmentedString(string, getSpecialStrings(), formatterMode);
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public VariableStore getVariableStore() {
		return variableStore;
	}

	public String getBestMatch(String tail) {

		Collection<String> keys = getSpecialStrings();

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
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (getClass() != obj.getClass()) return false;

		SequenceFactory that = (SequenceFactory) obj;
		return featureModel.equals(that.featureModel) &&
						formatterMode == that.formatterMode &&
						reservedStrings.equals(that.reservedStrings) &&
						variableStore.equals(that.variableStore);
	}

	@Override
	public int hashCode() {
		int result = 1175;
		result = 31 * result + featureModel.hashCode();
		result = 31 * result + variableStore.hashCode();
		result = 31 * result + formatterMode.hashCode();
		result = 31 * result + reservedStrings.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "SequenceFactory{" +
			", featureModel=" + featureModel +
			", variableStore=" + variableStore +
			", formatterMode=" + formatterMode +
			", reservedStrings=" + reservedStrings +
			'}';
	}

	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(variableStore.getKeys());
		keys.addAll(featureModel.getSymbols());
		keys.addAll(reservedStrings);
		return keys;
	}
}
