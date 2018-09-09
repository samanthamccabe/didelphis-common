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

package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;

import java.util.List;

/**
 * Class {@code StringParser}
 *
 * A {@link String}-only companion to {@link SequenceParser} which uses the same
 * linguistics-oriented regular expression syntax.
 *
 * @author Samantha Fiona McCabe
 * @since 0.3.0
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StringParser extends AbstractDidelphisParser<String> {

	static String WORD_START = "#[";
	static String WORD_END   = "]#";
	static String EPSILON    = "𝜆" ;
	static String DOT        = "." ;
	MultiMap<String, String> specials;

	public StringParser() {
		this(new GeneralMultiMap<>());
	}

	public StringParser(@NonNull MultiMap<String, String> specials) {
		this.specials = specials;
	}

	@NonNull
	@Override
	public String getWordStart() {
		return WORD_START;
	}

	@NonNull
	@Override
	public String getWordEnd() {
		return WORD_END;
	}

	@NonNull
	@Override
	public String transform(String expression) {
		return expression;
	}

	@NonNull
	@Override
	public String epsilon() {
		return EPSILON;
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecials() {
		return specials;
	}

	@NonNull
	@Override
	public String getDot() {
		return DOT;
	}

	@Override
	public int lengthOf(@NonNull String segments) {
		return segments.length();
	}

	@Override
	@NonNull
	public List<String> split(String substring) {
		return Splitter.toList(substring, DELIMITERS, specials.keys());
	}

	@NonNull
	@Override
	public String subSequence(String sequence, int start, int end) {
		return sequence.substring(start, end);
	}
}
