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

import lombok.EqualsAndHashCode;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.contracts.Streamable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Samantha Fiona McCabe
	 * @date 1/17/2015
 */
@EqualsAndHashCode
public class Lexicon<T> implements Streamable<List<Sequence<T>>> {

	private final Collection<List<Sequence<T>>> lexicon;

	@NotNull
	public static <T> Lexicon<T> fromSingleColumn(
			@NotNull SequenceFactory<T> factory, @NotNull Iterable<String> list
	) {
		Lexicon<T> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<T> sequence = factory.toSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	@NotNull
	public static <T> Lexicon<T> fromRows(
			@NotNull SequenceFactory<T> factory,
			@NotNull Iterable<List<String>> lists
	) {
		Lexicon<T> lexicon = new Lexicon<>();

		for (Iterable<String> row : lists) {
			List<Sequence<T>> lexRow = new ArrayList<>();
			for (String entry : row) {
				Sequence<T> sequence = factory.toSequence(entry);
				lexRow.add(sequence);
			}
			lexicon.add(lexRow);
		}
		return lexicon;
	}

	public Lexicon() {
		lexicon = new ArrayList<>();
	}

	public Lexicon(@NotNull Iterable<List<Sequence<T>>> iterable) {
		lexicon = new ArrayList<>();
		for (List<Sequence<T>> sequences : iterable) {
			lexicon.add(new ArrayList<>(sequences));
		}
	}

	public void add(@NotNull Sequence<T> sequence) {
		List<Sequence<T>> row = new ArrayList<>();
		row.add(sequence);
		lexicon.add(row);
	}

	public void add(@NotNull List<Sequence<T>> row) {
		lexicon.add(row);
	}

	@NotNull
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<List<Sequence<T>>> iterator = lexicon.iterator();
		while (iterator.hasNext()) {
			Collection<Sequence<T>> line = iterator.next();
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

	@NotNull
	@Override
	public Iterator<List<Sequence<T>>> iterator() {
		return lexicon.iterator();
	}
}
