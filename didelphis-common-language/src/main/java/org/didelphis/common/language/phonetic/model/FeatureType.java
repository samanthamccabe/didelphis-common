/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.language.phonetic.model;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 7/21/2016
 */
public enum FeatureType {
	BINARY(Pattern.compile("[+\\-\\u2212]")),
	TERNARY(Pattern.compile("[+0\\-\\u2212]")),
	NUMERIC(Pattern.compile("([\\-\\u2212]?\\d+(\\.\\d+)?)"));

	private final Pattern pattern;

	FeatureType(Pattern pattern) {
		this.pattern = pattern;
	}

	public static FeatureType find(CharSequence string) {
		String upperCase = string.toString().toUpperCase();
		return Arrays.stream(values())
				.filter(type -> type.name().equals(upperCase))
				.findFirst()
				.orElse(null);
	}
	
	public boolean matches(CharSequence value) {
		return pattern.matcher(value).matches();
	}
}
