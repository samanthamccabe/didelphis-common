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

import java.util.Arrays;
import java.util.Collection;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Enum {@code DoubleFeature}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
public enum DoubleFeature implements FeatureType<Double> {
	INSTANCE;

	private static final Collection<Double> UNDEFINED = Arrays.asList(
			null,
			Double.NaN,
			Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY
	);

	@Override
	@NonNull
	public FeatureModelLoader<Double> emptyLoader() {
		return new FeatureModelLoader<>(INSTANCE);
	}

	@NonNull
	@Override
	public Double parseValue(@NonNull String string) {
		Form form = Form.NFKC;
		String normalized = normalize(string, form);
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

	@NonNull
	@Override
	public Collection<Double> listUndefined() {
		return UNDEFINED;
	}

	@Override
	public int compare(Double v1, Double v2) {
		double x = v1 == null ? 0.0 : v1;
		double y = v2 == null ? 0.0 : v2;
		return Double.compare(x, y);
	}

	@Override
	public double difference(Double v1, Double v2) {
		return Math.abs(checkValue(v1) - (checkValue(v2)));
	}

	@Override
	public int intValue(Double value) {
		return (value == null) ? 0 : value.intValue();
	}

	@Override
	public double doubleValue(Double value) {
		return (value == null) ? Double.NaN : value;
	}


	@Override
	public String toString() {
		return "DoubleFeature";
	}

	private double checkValue(Double value) {
		return isDefined(value) ? value : 0.0;
	}
}
