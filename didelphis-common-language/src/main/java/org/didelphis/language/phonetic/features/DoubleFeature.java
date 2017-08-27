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

package org.didelphis.language.phonetic.features;

import org.didelphis.io.NullFileHandler;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Enum {@code DoubleFeature}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-06-12
 * @since 0.1.0
 */
public enum DoubleFeature implements FeatureType<Double> {
	INSTANCE;

	private static final Collection<Double> LIST = Arrays.asList(
			null,
			Double.NaN,
			Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY
	);

	public static FeatureModelLoader<Double> emptyLoader() {
		return new FeatureModelLoader<>(INSTANCE, NullFileHandler.INSTANCE, "");
	}

	@NotNull
	@Override
	public Double parseValue(@NotNull String string) {
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

	@NotNull
	@Override
	public Collection<Double> listUndefined() {
		return LIST;
	}

	@Override
	public int compare(Double v1, Double v2) {
		return Double.compare(v1, v2);
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

	private double checkValue(Double value) {
		return isDefined(value) ? value : 0.0;
	}
}
