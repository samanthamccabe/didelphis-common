/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
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

package org.didelphis.language.phonetic;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;

public abstract class PhoneticTestBase {

	protected static final FeatureModelLoader<Integer> loader = new FeatureModelLoader<>(
			IntegerFeature.INSTANCE,
			ClassPathFileHandler.INSTANCE,
			"AT_hybrid.model");
	
	protected static final SequenceFactory<Integer> factory = new SequenceFactory<>(
			loader.getFeatureMapping(),
			FormatterMode.INTELLIGENT);
}
