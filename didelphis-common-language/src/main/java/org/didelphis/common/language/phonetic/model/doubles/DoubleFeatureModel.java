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

import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.AbstractFeatureModel;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Map;

/**
 * Class {@code DoubleFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2016-07-02
 */
public final class DoubleFeatureModel extends AbstractFeatureModel<Double> {

	private static final Logger LOG = LoggerFactory.getLogger(DoubleFeatureModel.class);

	/**
	 * @param specification
	 * @param constraints
	 * @param aliases
	 */
	public  DoubleFeatureModel(FeatureSpecification specification,
			List<Constraint<Double>> constraints,
			Map<String, FeatureArray<Double>> aliases) {
		super(specification, constraints, aliases);
	}
	
	@Override
	public String toString() {
		return "DoubleFeatureSpecification{" + size() + '}';
	}

	@NotNull
	@Override
	public Double parseValue(@NotNull String string) {
		Form form = Form.NFKC;
		String normalized = Normalizer.normalize(string, form);
		if (normalized.equals("+")) {
			return 1.0;
		} else if (normalized.equals("-")) {
			return -1.0;
		} else if (string.isEmpty()) {
			return Double.NaN;
		} else {
			return Double.valueOf(normalized);
		}
	}

	@Override
	public boolean isDefined(@Nullable Double value) {
		return value != null && !Double.isNaN(value) && Double.isFinite(value);
	}

	@Override
	public int compare(Double v1, Double v2) {
		return Double.compare(v1, v2);
	}

}
