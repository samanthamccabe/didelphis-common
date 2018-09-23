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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Enum {@code BinaryFeatureType}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
public enum BinaryFeature implements FeatureType<Boolean> {
	INSTANCE;

	public static final Set<Boolean> UNDEFINED = Collections.singleton(null);

	@Override
	@NonNull
	public FeatureModelLoader<Boolean> emptyLoader() {
		return new FeatureModelLoader<>(INSTANCE);
	}
	
	@NonNull
	@Override
	public Boolean parseValue(@NonNull String string) {
		String normalized = normalize(string, Form.NFKC);
		if (normalized.isEmpty()) {
			return Boolean.FALSE;
		}
		if (normalized.equals("-") || normalized.equals("0")) {
			return Boolean.FALSE;
		}
		if (normalized.equals("+") || normalized.equals("1")) {
			return Boolean.TRUE;
		}
		throw new NumberFormatException("Unrecognized boolean representation "
				+ string);
	}

	@NonNull
	@Override
	public Collection<Boolean> listUndefined() {
		return UNDEFINED;
	}

	@Override
	public int compare(Boolean v1, Boolean v2) {
		return Boolean.compare(validate(v1), validate(v2));
	}

	@Override
	public double difference(Boolean v1, Boolean v2) {
		return validate(v1) ^ validate(v2) ? 1.0 : 0.0;
	}

	@Override
	public int intValue(Boolean value) {
		return (validate(value) && value) ? 1 : 0;
	}

	@Override
	public double doubleValue(Boolean value) {
		return intValue(value);
	}
	
	@Override
	public String toString() {
		return "BinaryFeature";
	}
	private boolean validate(Boolean v) {
		return isDefined(v) ? v : false;
	}
}
