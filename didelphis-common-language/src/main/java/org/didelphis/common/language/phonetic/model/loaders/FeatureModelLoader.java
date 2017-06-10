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

package org.didelphis.common.language.phonetic.model.loaders;

import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.DefaultFeatureSpecification;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samantha on 2/18/17.
 */
public final class FeatureModelLoader {

	private static final Pattern FEATURES_PATTERN = Pattern.compile(
			"(\\w+)\\s+(\\w*)\\s*(ternary|binary|numeric(\\(-?\\d,\\d\\))?)",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern COMMENT_PATTERN = Pattern.compile("\\s*%.*");
	private static final Pattern NEWLINE = Pattern.compile("\r\n?|\n");
	private static final Pattern PARENS = Pattern.compile("\\(.*\\)");
	private static final Pattern TRANSFORM = Pattern.compile("\\s*>\\s*");
	private static final Pattern BRACKETS = Pattern.compile("[\\[\\]]");
	private static final Pattern EQUALS = Pattern.compile("\\s*=\\s*");

	private final List<String> featureNames;
	private final Map<String, Integer> featureIndices;
	private final Collection<String> rawConstraints;
	private final Collection<String> rawAliases;

	private FeatureModelLoader() {
		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();
		
		rawConstraints = new ArrayList<>();
		rawAliases = new ArrayList<>();
	}
	
	private FeatureSpecification parseSpecification(Iterable<String> data) {
		// 1. parse the fields, create raw representations
		// 2. instantiate the featureModel object w size
		// 3. create correct internal objects with instance
		// 4. add objects back to instance
		Collection<String> featureZone = new ArrayList<>();
		ParseZone currentZone = ParseZone.NONE;
		for (String string : data) {
			String line = COMMENT_PATTERN.matcher(string).replaceAll("").trim();
			
			if (line.isEmpty()) {
				continue;
			}

			ParseZone zone = ParseZone.determineZone(line);
			if (zone == null) {
				//noinspection EnumSwitchStatementWhichMissesCases
				switch (currentZone) { // Should not be possible to be null
					case SPECIFICATION:
						break;
					case FEATURES:
						featureZone.add(line.toLowerCase());
						break;
					case CONSTRAINTS:
						rawConstraints.add(line);
						break;
					case SYMBOLS:
						break;
					case MODIFIERS:
						break;
					case ALIASES:
						rawAliases.add(line);
						break;
					case NONE:
						break;
				}
			} else {
				currentZone = zone;
			}
		}
		populateFeatures(featureZone);
		// Once the main feature definitions are parsed, it's possible to create
		// the featureModel instance
		return new DefaultFeatureSpecification(featureNames, featureIndices);
	}

	public static FeatureModel<Double> loadDouble(Iterable<String> lines) {
		FeatureModelLoader loader = new FeatureModelLoader();
		FeatureSpecification spec = loader.parseSpecification(lines);
		return loader.populateDoubleModel(spec);
	}
	
	public static FeatureModel<Double> loadDouble(String path, FileHandler handler) {
		List<String> list = Arrays.asList(NEWLINE.split(handler.read(path)));
		return loadDouble(list);
	}

	private  FeatureModel<Double> populateDoubleModel(FeatureSpecification spec) {
		Map<String, FeatureArray<Double>> aliases = new HashMap<>();
		List<Constraint<Double>> cons = new ArrayList<>();
		FeatureModel<Double> model = new DoubleFeatureModel(spec, cons, aliases);
		
		for (CharSequence string : rawAliases) {
			String[] split = EQUALS.split(string, 2);
			String alias = BRACKETS.matcher(split[0]).replaceAll("");
			String value = split[1];
			aliases.put(alias, model.parseFeatureString(value));
		}

		for (String entry : rawConstraints) {
			String[] split = TRANSFORM.split(entry, 2);
			String source = split[0];
			String target = split[1];
			FeatureArray<Double> sMap = model.parseFeatureString(source);
			FeatureArray<Double> tMap = model.parseFeatureString(target);
			cons.add(new Constraint<>(entry, sMap, tMap, model));
		}
		return model;
	}

	@Override
	public String toString() {
		return "FeatureModelLoader"
		       + "{ featureNames=" + featureNames
		       + ", featureIndices=" + featureIndices
		       + ", rawConstraints=" + rawConstraints
		       + ", rawAliases=" + rawAliases
		       + " }";
	}

	private void populateFeatures(Iterable<String> featureZone) {
		int i = 0;
		for (String entry : featureZone) {
			Matcher matcher = FEATURES_PATTERN.matcher(entry);
			if (matcher.matches()) {
				String name = matcher.group(1);
				String alias = matcher.group(2);
				// Ignore value range checks for now
				featureNames.add(name);
				featureIndices.put(name, i);
				if (!alias.isEmpty()) {
					featureIndices.put(alias, i);
				}
			} else {
				throw new ParseException(
						"Unrecognized command in FEATURE block", entry);
			}
			i++;
		}
	}


}
