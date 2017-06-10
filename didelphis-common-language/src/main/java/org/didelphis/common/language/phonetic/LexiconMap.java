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

package org.didelphis.common.language.phonetic;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/16/2015
 */
public class LexiconMap<N> {

	private final Map<String, String>  paths;
	private final Map<String, Lexicon<N>> lexicons;

	public LexiconMap() {
		paths = new LinkedHashMap<>();
		lexicons = new LinkedHashMap<>();
	}

	public void addLexicon(String handle, String path, Lexicon<N> words) {
		paths.put(handle, path);
		lexicons.put(handle, words);
	}

	public void addAll(LexiconMap<N> map) {
		paths.putAll(map.paths);
		lexicons.putAll(map.lexicons);
	}

	public Lexicon<N> getLexicon(String handle) {
		return lexicons.get(handle);
	}

	public String getPath(String handle) {
		return paths.get(handle);
	}

	public boolean hasHandle(String handle) {
		return lexicons.containsKey(handle);
	}

	public Collection<String> getHandles() {
		return lexicons.keySet();
	}

	public Collection<Lexicon<N>> values() {
		return lexicons.values();
	}

	public Lexicon<N> remove(String handle) {
		paths.remove(handle);
		return lexicons.remove(handle);
	}

	@Override
	public String toString() {
		return "LexiconMap:" + lexicons;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LexiconMap)) return false;
		LexiconMap<?> that = (LexiconMap<?>) o;
		return Objects.equals(paths, that.paths)
		       && Objects.equals(lexicons, that.lexicons);
	}

	@Override
	public int hashCode() {
		return Objects.hash(paths, lexicons);
	}
}
