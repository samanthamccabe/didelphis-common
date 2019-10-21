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

import java.util.Arrays;
import java.util.Collection;

import static java.text.Normalizer.*;

/**
 * Enum {@code DoubleFeature}
 *
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
	public int compare(@Nullable Double v1, @Nullable Double v2) {
		double x = v1 == null ? 0.0 : v1;
		double y = v2 == null ? 0.0 : v2;
		return Double.compare(x, y);
	}

	@Override
	public double difference(@Nullable Double v1, @Nullable Double v2) {
		return Math.abs(norm(v1) - (norm(v2)));
	}

	@Override
	public int intValue(@Nullable Double value) {
		return (value == null) ? 0 : value.intValue();
	}

	@Override
	public double doubleValue(@Nullable Double value) {
		return (value == null) ? Double.NaN : value;
	}

	@Override
	public String toString() {
		return "DoubleFeature";
	}

	private double norm(Double value) {
		return isDefined(value) ? value : 0.0;
	}
}
