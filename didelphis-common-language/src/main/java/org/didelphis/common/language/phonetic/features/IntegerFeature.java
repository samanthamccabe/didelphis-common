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

package org.didelphis.common.language.phonetic.features;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.text.Normalizer.*;

/**
 * Class {@code IntegerFeature}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-12
 */
public enum IntegerFeature implements FeatureType<Integer> {
	INSTANCE;

	@NotNull
	@Override
	public Integer parseValue(@NotNull String string) {
		String normalize = normalize(string, Form.NFKC);
		if (normalize.equals("+")) {
			return 1;
		} else if (normalize.equals("-")) {
			return -1;
		} else if (string.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(normalize);
		}
	}

	@Override
	public boolean isDefined(@Nullable Integer value) {
		return value != null;
	}

	@Override
	public int compare(Integer v1, Integer v2) {
		int x = v1 == null ? Integer.MIN_VALUE:v1;
		int y = v2 == null ? Integer.MIN_VALUE:v2;
		return Integer.compare(x, y);
	}

	@Override
	public double difference(Integer v1, Integer v2) {
		return Math.abs(checkValue(v1) - checkValue(v2));
	}

	private Integer checkValue(Integer value) {
		return isDefined(value) ? value : 0;
	}
}
