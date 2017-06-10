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

package org.didelphis.common.language.phonetic.model.doubles;

import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.AbstractFeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.AbstractFeatureMappingLoader;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2017-02-28
 */
public final class DoubleFeatureMapping extends AbstractFeatureMapping<Double> {
	
	private DoubleFeatureMapping(FeatureModel<Double> featureModel,
			Map<String, FeatureArray<Double>> featureMap,
			Map<String, FeatureArray<Double>> modifiers) {
		super(featureModel, featureMap, modifiers);
	}

	public static DoubleFeatureMapping load(String path, FileHandler handler,
			FormatterMode mode) {
		Loader loader = new Loader(path, handler, mode);
		return new DoubleFeatureMapping(loader.getFeatureModel(),
				loader.getFeatureMap(),
				loader.getDiacritics());
	}

	@Override
	protected double difference(Double x, Double y) {
		return Math.abs(x - y);
	}

	private static final class Loader
			extends AbstractFeatureMappingLoader<Double> {

		private Loader(String path, FileHandler handler, FormatterMode mode) {
			super(path, handler, mode);
		}

		@Override
		protected FeatureModel<Double> loadModel(String path) {
			return FeatureModelLoader.loadDouble(path, fileHandler);
		}

		@Override
		protected FeatureModel<Double> loadModel(List<String> lines) {
			return FeatureModelLoader.loadDouble(lines);
		}
	}
}
