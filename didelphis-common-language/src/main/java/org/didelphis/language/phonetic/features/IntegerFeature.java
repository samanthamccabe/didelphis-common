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

package org.didelphis.language.phonetic.features;

import lombok.NonNull;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Class {@code IntegerFeature}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
public enum IntegerFeature implements FeatureType<Integer> {
	INSTANCE;

	public static FeatureModelLoader<Integer> emptyLoader() {
		return new FeatureModelLoader<>(INSTANCE);
	}

	@NonNull
	@Override
	public Integer parseValue(@NonNull String string) {
		String normalized = normalize(string, Form.NFKC);
		if (normalized.equals("+")) {
			return 1;
		} else if (normalized.equals("-")) {
			return -1;
		} else if (string.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(normalized);
		}
	}

	@NonNull
	@Override
	public Collection<Integer> listUndefined() {
		return Collections.singleton(null);
	}

	@Override
	public int compare(@Nullable Integer v1, @Nullable Integer v2) {
		int x = v1 == null ? 0 : v1;
		int y = v2 == null ? 0 : v2;
		return Integer.compare(x, y);
	}

	@Override
	public double difference(@Nullable Integer v1, @Nullable Integer v2) {
		return Math.abs(checkValue(v1) - checkValue(v2));
	}

	@Override
	public int intValue(Integer value) {
		return (value == null) ? 0 : value;
	}

	@Override
	public double doubleValue(Integer value) {
		return (value == null) ? Double.NaN : value.doubleValue();
	}
	
	@Override
	public String toString() {
		return "IntegerFeature";
	}

	@NonNull
	private Integer checkValue(@Nullable Integer value) {
		if (value == null) {
			return 0;
		}
		return isDefined(value) ? value : 0;
	}
}
