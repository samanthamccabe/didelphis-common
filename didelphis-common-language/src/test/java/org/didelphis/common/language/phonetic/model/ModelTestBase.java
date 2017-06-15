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

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.features.IntegerFeature;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;

/**
 * Created by samantha on 10/10/15.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class ModelTestBase {

	protected static SequenceFactory<Integer> loadFactory(String resourceName, FormatterMode mode) {
		return new SequenceFactory<>(loadMapping(resourceName, mode), mode);
	}

	protected static FeatureMapping<Integer> loadMapping(String resourceName, FormatterMode mode) {
		FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				resourceName);
		return loader.getFeatureMapping();
	}
}
