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

package org.didelphis.utilities;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility Class {@code PatternUtils}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-24
 * @since 0.1.0
 */
@SuppressWarnings("TypeMayBeWeakened")
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatternUtils {

	Pattern WHITESPACE = Pattern.compile("\\s+");

	@NonNull
	public String cleanSpaces(@NonNull CharSequence charSequence) {
		return WHITESPACE.matcher(charSequence).replaceAll(" ");
	}

	@NonNull
	public Pattern template(@NonNull String head, @NonNull String... vars) {
		String regex = head;
		for (int i = 0; i < vars.length; i++) {
			regex = regex.replace("$" + (i + 1), vars[i]);
		}
		return Pattern.compile(regex);
	}

	@NonNull
	public Pattern compile(@NonNull String head, @NonNull String... tail) {
		String regex = concat(head, tail);
		return Pattern.compile(regex);
	}

	@NonNull
	private String concat(@NonNull String head, @NonNull String... tail) {
		return Arrays.stream(tail).collect(Collectors.joining("", head, ""));
	}
}
