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


@EqualsAndHashCode
public class Lexicon implements Streamable<List<Sequence>> {

	private final Collection<List<Sequence>> lexicon;

	@NonNull
	public static Lexicon fromSingleColumn(
			@NonNull SequenceFactory factory, @NonNull Iterable<String> list
	) {
		Lexicon lexicon = new Lexicon();
		for (String entry : list) {
			Sequence sequence = factory.toSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	@NonNull
	public static Lexicon fromRows(
			@NonNull SequenceFactory factory,
			@NonNull Iterable<List<String>> lists
	) {
		Lexicon lexicon = new Lexicon();

		for (Iterable<String> row : lists) {
			List<Sequence> lexRow = new ArrayList<>();
			for (String entry : row) {
				Sequence sequence = factory.toSequence(entry);
				lexRow.add(sequence);
			}
			lexicon.add(lexRow);
		}
		return lexicon;
	}

	public Lexicon() {
		lexicon = new ArrayList<>();
	}

	public Lexicon(@NonNull Iterable<List<Sequence>> iterable) {
		lexicon = new ArrayList<>();
		for (List<Sequence> sequences : iterable) {
			lexicon.add(new ArrayList<>(sequences));
		}
	}

	public void add(@NonNull Sequence sequence) {
		List<Sequence> row = new ArrayList<>();
		row.add(sequence);
		lexicon.add(row);
	}

	public void add(@NonNull List<Sequence> row) {
		lexicon.add(row);
	}

	@NonNull
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<List<Sequence>> iterator = lexicon.iterator();
		while (iterator.hasNext()) {
			Collection<Sequence> line = iterator.next();
			Iterator<Sequence> it = line.iterator();
			while (it.hasNext()) {
				Sequence sequence = it.next();
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
	public Iterator<List<Sequence>> iterator() {
		return lexicon.iterator();
	}
}
