/******************************************************************************
 * Copyright (c) 2016. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.phonetic.model.loaders;

import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.FeatureType;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samantha on 4/27/15.
 */
public abstract class AbstractFeatureMappingLoader<N extends Number> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFeatureMappingLoader.class);

	private static final String SYMBOLS     = "SYMBOLS";
	private static final String MODIFIERS   = "MODIFIERS";
	private static final String ZONE_STRING = SYMBOLS + '|' + MODIFIERS;

	private static final Pattern COMMENT_PATTERN = Pattern.compile("\\s*%.*");
	private static final Pattern ZONE_PATTERN    = Pattern.compile(ZONE_STRING);

	private static final Pattern SYMBOL_PATTERN = Pattern.compile("([^\\t]+)\\t(.*)");
	private static final Pattern TAB_PATTERN    = Pattern.compile("\\t");
	private static final Pattern SPEC_PATTERN   = Pattern.compile("SPECIFICATION:\\s\"([^\"]+)\"");
	private static final Pattern DOTTED_CIRCLE  = Pattern.compile("\u25CC", Pattern.LITERAL);
	private static final Pattern NEWLINE        = Pattern.compile("\r?\n|\r");

	protected final String sourcePath;
	protected final FileHandler fileHandler;
	
	private final FormatterMode formatterMode;
	private final Map<String, FeatureArray<N>> featureMap;
	private final Map<String, FeatureArray<N>> diacritics;
	private FeatureModel<N> featureModel;
	
	protected AbstractFeatureMappingLoader(String path, FileHandler handler,
			FormatterMode mode) {
		sourcePath = path;
		featureMap = new LinkedHashMap<>();
		diacritics = new LinkedHashMap<>();
		formatterMode = mode;
		fileHandler = handler;
		parseLines();
	}
	
	@Override
	public String toString() {
		return "FeatureModelLoader{" + sourcePath + '}';
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Map<String, FeatureArray<N>> getFeatureMap() {
		return featureMap;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Map<String, FeatureArray<N>> getDiacritics() {
		return diacritics;
	}

	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}

	private void parseLines() {

		featureModel = null;

		List<String> specZone = new ArrayList<>();
		List<String> symbolZone = new ArrayList<>();
		List<String> modifierZone = new ArrayList<>();
		
		/* Probably what we need to do here is use the zones to capture every
		 * line up to the next zone or EOF. Put these in lists, one for each
		 * zone. Then parse each zone separately. This will reduce cyclomatic
		 * complexity and should avoid redundant checks.
		 */
		String currentZone = "";
		for (String string : NEWLINE.split(fileHandler.read(sourcePath))) {
			if (string.isEmpty() || string.startsWith("%")) { continue; }
			// Remove comments
			String line = COMMENT_PATTERN.matcher(string).replaceAll("");
			Matcher specMatcher = SPEC_PATTERN.matcher(line);
			Matcher zoneMatcher = ZONE_PATTERN.matcher(line);
			if (zoneMatcher.find()) {
				currentZone = zoneMatcher.group(0);
			} else if (specMatcher.find()) {
				String path = specMatcher.group(1);
//				featureModel = FeatureModelLoader.loadDouble(path, fileHandler);
				featureModel = loadModel(path);
			} else if (!line.isEmpty() && !line.trim().isEmpty()) {
				if (currentZone.isEmpty()) {
					specZone.add(line);
				} else if (currentZone.equals(SYMBOLS)) {
					symbolZone.add(line);
				} else if (currentZone.equals(MODIFIERS)) {
					modifierZone.add(line);
				}
			} else {
				//TODO: ignore and log (at the end?)
			}
		}
		
		if (featureModel == null) {
//			featureModel = FeatureModelLoader.loadDouble(specZone);
			featureModel = loadModel(specZone);
		}
		
		// Now parse each of the lists
		populateSymbols(symbolZone);
		populateModifiers(modifierZone);
	}

	/**
	 * Specifies how a given symbol is to be interpreted (e.g. + as 1, - as 0)
	 * @param string the value to be read
	 * @param defaultValue the value used for missing data
	 * @return the value for that feature symbol
	 */
	protected abstract N getValue(String string, N defaultValue);
	
	protected abstract FeatureModel<N> loadModel(String path);
	
	protected abstract FeatureModel<N> loadModel(List<String> lines);

	private void populateModifiers(Iterable<String> modifierZone) {
		for (String entry : modifierZone) {
			Matcher matcher = SYMBOL_PATTERN.matcher(entry);
			if (matcher.matches()) {
				String symbol = matcher.group(1);
				String[] values = TAB_PATTERN.split(matcher.group(2), -1);
				FeatureArray<N> array = new SparseFeatureArray<>(featureModel);
				int i = 0;
				for (String value : values) {
					if (!value.isEmpty()) {
						array.set(i, getValue(value, null));
					}
					i++;
				}
				String diacritic = DOTTED_CIRCLE.matcher(symbol).replaceAll("");
				diacritics.put(diacritic, array);
			} else {
				LOG.error("Unrecognized diacritic definition {}", entry);
			}
		}
	}

	private void populateSymbols(Iterable<String> symbolZone) {
		for (String entry : symbolZone) {
			Matcher matcher = SYMBOL_PATTERN.matcher(entry);

			if (matcher.matches()) {
				String symbol = formatterMode.normalize(matcher.group(1));
				String[] values = TAB_PATTERN.split(matcher.group(2), -1);

				int size = featureModel.size();

				List<FeatureType> featureTypes = featureModel.getFeatureTypes();
				FeatureArray<N> features = new SparseFeatureArray<>(featureModel);

				for (int i = 0; i < size; i++) {
					FeatureType type = featureTypes.get(i);
					String value = values[i];
					if (!type.matches(value)) {
						LOG.warn("Value '{}' at position {} is not valid for {}" 
						         + " in array: {}", value, i, type, 
								Arrays.toString(values));
					}
					features.set(i, getValue(value, null));
				}
				checkFeatureCollisions(symbol, features);
				featureMap.put(symbol, features);
			} else {
				LOG.error("Unrecognized symbol definition {}", entry);
			}
		}
	}

	private void checkFeatureCollisions(String symbol,
			FeatureArray<N> features) {
		if (featureMap.containsValue(features)) {
			for (Map.Entry<String, FeatureArray<N>> e : featureMap.entrySet()) {
				if (features.equals(e.getValue())) {
					LOG.warn("Collision between features {} and {} --- both " 
					         + "have value {}", symbol, e.getKey(), features);
				}
			}
		}
	}
}
