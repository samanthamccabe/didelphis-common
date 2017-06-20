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

package org.didelphis.language.phonetic;

import org.didelphis.language.phonetic.sequences.Sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Samantha Fiona McCabe
 * Date: 1/17/2015
 */
public class Lexicon<T> implements Iterable<List<Sequence<T>>> {

	private final List<List<Sequence<T>>> lexicon;

	public Lexicon() {
		lexicon = new ArrayList<>();
	}

	public void add(Sequence<T> sequence) {
		List<Sequence<T>> row = new ArrayList<>();
		row.add(sequence);
		lexicon.add(row);
	}

	public void add(List<Sequence<T>> row ) {
		lexicon.add(row);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<List<Sequence<T>>> iterator = lexicon.iterator();
		while (iterator.hasNext()) {
			List<Sequence<T>> line = iterator.next();
			Iterator<Sequence<T>> it = line.iterator();
			while (it.hasNext()) {
				Sequence<T> sequence = it.next();
				sb.append(sequence);
				if (it.hasNext()) {
					sb.append("\\t");
				}
			}
			if (iterator.hasNext()) {
				sb.append("\\n");
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true;  }
		if (o == null) { return false; }
		if (getClass() != o.getClass()) { return false; }

		Lexicon<?> lexicon1 = (Lexicon<?>) o;
		return lexicon.equals(lexicon1.lexicon);
	}

	@Override
	public int hashCode() {
		return 11 * lexicon.hashCode();
	}

	@Override
	public Iterator<List<Sequence<T>>> iterator() {
		return lexicon.iterator();
	}
}
