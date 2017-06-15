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

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Class {@code BinaryFeatureType}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-11
 */
public enum BinaryFeature implements FeatureType<Boolean> {
	INSTANCE;

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

	@Override
	public double difference(Boolean v1, Boolean v2) {
		return validate(v1) ^ validate(v2) ? 1.0 : 0.0;
	}

	private boolean validate(Boolean v) {
		return isDefined(v) ? v : false;
	}
}
