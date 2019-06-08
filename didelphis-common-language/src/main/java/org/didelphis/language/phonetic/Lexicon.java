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

package org.didelphis.language.phonetic;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.contracts.Streamable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
	 */
@EqualsAndHashCode
public class Lexicon<T> implements Streamable<List<Sequence<T>>> {

	private final Collection<List<Sequence<T>>> lexicon;

	@NonNull
	public static <T> Lexicon<T> fromSingleColumn(
			@NonNull SequenceFactory<T> factory, @NonNull Iterable<String> list
	) {
		Lexicon<T> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<T> sequence = factory.toSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	@NonNull
	public static <T> Lexicon<T> fromRows(
			@NonNull SequenceFactory<T> factory,
			@NonNull Iterable<List<String>> lists
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

	public Lexicon(@NonNull Iterable<List<Sequence<T>>> iterable) {
		lexicon = new ArrayList<>();
		for (List<Sequence<T>> sequences : iterable) {
			lexicon.add(new ArrayList<>(sequences));
		}
	}

	public void add(@NonNull Sequence<T> sequence) {
		List<Sequence<T>> row = new ArrayList<>();
		row.add(sequence);
		lexicon.add(row);
	}

	public void add(@NonNull List<Sequence<T>> row) {
		lexicon.add(row);
	}

	@NonNull
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

	@NonNull
	@Override
	public Iterator<List<Sequence<T>>> iterator() {
		return lexicon.iterator();
	}
}
