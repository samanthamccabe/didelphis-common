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
 * Enum {@code BinaryFeatureType}
 *
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
	public int compare(@Nullable Boolean v1, @Nullable Boolean v2) {
		return Boolean.compare(isValid(v1), isValid(v2));
	}

	@Override
	public double difference(@Nullable Boolean v1, @Nullable Boolean v2) {
		return isValid(v1) ^ isValid(v2) ? 1.0 : 0.0;
	}

	@Override
	public int intValue(@Nullable Boolean value) {
		return (isValid(value) && value) ? 1 : 0;
	}

	@Override
	public double doubleValue(@Nullable Boolean value) {
		return intValue(value);
	}

	@Override
	public String toString() {
		return "BinaryFeature";
	}

	private boolean isValid(@Nullable Boolean v) {
		return v != null && isDefined(v) ? v : false;
	}
}
