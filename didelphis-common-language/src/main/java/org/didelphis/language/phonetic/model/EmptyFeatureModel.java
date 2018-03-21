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

package org.didelphis.language.phonetic.model;

import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class {@code EmptyFeatureModel}
 *
 * @author Samantha Fiona McCabe
 * @date 1/1/18
 */
@ToString
public enum EmptyFeatureModel implements FeatureModel<Object> {
	INSTANCE;
	
	@NonNull
	@Override
	public List<Constraint<Object>> getConstraints() {
		return Collections.emptyList();
	}

	@NonNull
	@Override
	public FeatureArray<Object> parseFeatureString(@NonNull String string) {
		return new EmptyFeatureArray<>(this);
	}

	@NonNull
	@Override
	public FeatureType<Object> getFeatureType() {
		return new EmptyFeatureType();
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return EmptyFeatureSpecification.INSTANCE;
	}

	private static final class EmptyFeatureType implements FeatureType<Object> {
		@NonNull
		@Override
		public Object parseValue(@NonNull String string) {
			return 0;
		}

		@NonNull
		@Override
		public Collection<Object> listUndefined() {
			return Collections.emptyList();
		}

		@Override
		public int compare(@Nullable Object v1, @Nullable Object v2) {
			return 0;
		}

		@Override
		public double difference(@Nullable Object v1, @Nullable Object v2) {
			return 0;
		}

		@Override
		public int intValue(@Nullable Object value) {
			return 0;
		}

		@Override
		public double doubleValue(@Nullable Object value) {
			return 0;
		}
	}
}
