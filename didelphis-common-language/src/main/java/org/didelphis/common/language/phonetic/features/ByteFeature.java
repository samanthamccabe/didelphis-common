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
 * Class {@code ByteFeature}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-12
 */
public enum ByteFeature implements FeatureType<Byte> {
	INSTANCE;

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

	@Override
	public boolean isDefined(@Nullable Byte value) {
		return value != null;
	}

	@Override
	public int compare(Byte v1, Byte v2) {
		return Byte.compare(v1, v2);
	}

	@Override
	public double difference(@Nullable Byte v1, @Nullable Byte v2) {
		return Math.abs(validate(v1) - validate(v2));
	}

	private byte validate(Byte t) {
		return isDefined(t) ? t : 0;
	}
}
