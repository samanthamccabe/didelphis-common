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

package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.model.doubles.DoubleFeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;

/**
 * Created by samantha on 10/10/15.
 */
public abstract class ModelTestBase {

	protected static SequenceFactory<Double> loadFactory(String resourceName, FormatterMode mode) {
		return new SequenceFactory<>(loadMapping(resourceName, mode), mode);
	}

	protected static FeatureMapping<Double> loadMapping(String resourceName, FormatterMode mode) {
//		InputStream stream = ModelTestBase.class.getClassLoader().getResourceAsStream(resourceName);
//		return new DefaultFeatureMapping<>(stream, mode);
		FileHandler handler = ClassPathFileHandler.INSTANCE;
		return DoubleFeatureMapping.load(resourceName, handler, mode);
	}
}
