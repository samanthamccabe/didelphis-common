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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Enum {@code ByteFeature}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-06-12
 * @since 0.1.0
 */
public enum ByteFeature implements FeatureType<Byte> {
	INSTANCE;

	public static FeatureModelLoader<Byte> emptyLoader() {
		return new FeatureModelLoader<>(INSTANCE, NullFileHandler.INSTANCE, "");
	}

	@NotNull
	@Override
	public Byte parseValue(@NotNull String string) {
		String normalize = normalize(string, Form.NFKC);
		if (normalize.equals("+")) {
			return 1;
		} else if (normalize.equals("-")) {
			return -1;
		} else if (string.isEmpty()) {
			return 0;
		} else {
			return Byte.parseByte(normalize);
		}
	}

	@NotNull
	@Override
	public Collection<Byte> listUndefined() {
		return Collections.singleton(null);
	}

	@Override
	public int compare(Byte v1, Byte v2) {
		return Byte.compare(v1, v2);
	}

	@Override
	public double difference(@Nullable Byte v1, @Nullable Byte v2) {
		return Math.abs(validate(v1) - validate(v2));
	}

	@Override
	public int intValue(Byte value) {
		return (value == null) ? 0 : value.intValue();
	}

	@Override
	public double doubleValue(Byte value) {
		return (value == null) ? Double.NaN : value.doubleValue();
	}

	private byte validate(Byte t) {
		return isDefined(t) ? t : 0;
	}
}
