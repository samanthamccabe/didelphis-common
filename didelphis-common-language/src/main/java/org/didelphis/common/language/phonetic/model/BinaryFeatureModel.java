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

import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Class BinaryFeatureModel
 *
 * @author samantha
 * @since 2017-06-09
 */
public class BinaryFeatureModel extends AbstractFeatureModel<Boolean> {

	private static FeatureModel<Boolean> EMPTY;
	public static FeatureModel<Boolean> getEmptyModel() {
		if (EMPTY == null) {
			EMPTY = new BinaryFeatureModel(DefaultFeatureSpecification.EMPTY,
					Collections.emptyList(),
					Collections.emptyMap());
		}
		return EMPTY;
	}

	/**
	 * @param specification
	 * @param constraints
	 * @param aliases
	 */
	public BinaryFeatureModel(FeatureSpecification specification,
			List<Constraint<Boolean>> constraints,
			Map<String, FeatureArray<Boolean>> aliases) {
		super(specification, constraints, aliases);
	}

	@NotNull
	@Override
	public Boolean parseValue(@NotNull String string) {
		String normalized = normalize(string, Form.NFKC);
		if (normalized.equals("-") || normalized.equals("0")) {
			return Boolean.FALSE;
		} else if (normalized.equals("+") || normalized.equals("1")) {
			return Boolean.TRUE;
		}
		throw new NumberFormatException("Unrecognized boolean representation " +
				string);
	}

	@Override
	public boolean isDefined(@Nullable Boolean value) {
		return value != null && value;
	}

	@Override
	public int compare(Boolean v1, Boolean v2) {
		return Boolean.compare(v1, v2);
	}

}
