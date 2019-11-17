/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.phonetic.features;

import lombok.NonNull;

import org.didelphis.language.phonetic.model.FeatureModelLoader;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.text.Normalizer.*;

/**
 * Class {@code IntegerFeature}
 *
 * @since 0.1.0
 */
public enum IntegerFeature implements FeatureType<Integer> {
	INSTANCE;

	public static final Set<Integer> UNDEFINED = Collections.singleton(null);

	@NonNull
	@Override
	public FeatureModelLoader<Integer> emptyLoader() {
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
		return UNDEFINED;
	}

	@Override
	public int compare(@Nullable Integer v1, @Nullable Integer v2) {
		int x = v1 == null ? 0 : v1;
		int y = v2 == null ? 0 : v2;
		return Integer.compare(x, y);
	}

	@Override
	public double difference(@Nullable Integer v1, @Nullable Integer v2) {
		return Math.abs(norm(v1) - norm(v2));
	}

	@Override
	public int intValue(@Nullable Integer value) {
		return (value == null) ? 0 : value;
	}

	@Override
	public double doubleValue(@Nullable Integer value) {
		return (value == null) ? Double.NaN : value.doubleValue();
	}

	@Override
	public String toString() {
		return "IntegerFeature";
	}

	@NonNull
	private Integer norm(@Nullable Integer value) {
		return value != null && isDefined(value) ? value : 0;
	}
}
