/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.didelphis.common.structures.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @param <E>
 *
 * @author Samantha Fiona Morrigan McCabe
 */
public class SymmetricTable<E> extends AbstractTable<E> {

	private final List<E> array;

	private SymmetricTable(int n) {
		super(n, n);
		array = new ArrayList<>(n + ((n * n) / 2));
	}

	public SymmetricTable(int n, List<E> array) {
		super(n, n);
		int size = n + ((n * n - n) / 2);
		if (array.size() == size) {
			this.array = new ArrayList<>(array);
		} else {
			throw new IllegalArgumentException(
					"Array was provided with size " + array.size() + " but " +
							"must be " + size
			);
		}
	}

	public SymmetricTable(E defaultValue, int n) {
		this(n);
		int number = getIndex(n, n) - 1;
		for (int i = 0; i < number; i++) {
			array.add(defaultValue);
		}
	}

	protected SymmetricTable(SymmetricTable<E> otherTable) {
		this(otherTable.getRows());
		array.addAll(otherTable.array);
	}

	@Override
	public E get(int col, int row) {
		return array.get(getIndex(col, row));
	}

	@Override
	public void set(int col, int row, E element) {
		int index = getIndex(col, row);
		array.set(index, element);
	}

	@Deprecated
	@Override
	public String formattedTable() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j <= i; j++) {
				sb.append(get(i, j));
				if (j < i) {
					sb.append('\t');
				}
			}
			if (i < (getRows() - 1)) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(array);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SymmetricTable)) return false;
		SymmetricTable<?> that = (SymmetricTable<?>) o;
		return Objects.equals(array, that.array) 
		       && getRows() == that.getRows() 
		       && getColumns() == that.getColumns();
	}

	@Override
	public String toString() {
		return "SymmetricTable{" + array + '}';
	}

	private static int getIndex(int i, int j) {
		return j > i ? getRowStart(j) + i : getRowStart(i) + j;
	}

	private static int getRowStart(int row) {
		return IntStream.rangeClosed(0, row).sum();
	}
}
