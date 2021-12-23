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

package org.didelphis.utilities;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;

/**
 * Class {@code Sort}
 */
@UtilityClass
public class Sort {

	public <T> void quicksort(List<? extends T> list, Comparator<T> comparator) {
		quicksort(list, comparator, 0, list.size());
	}

	private <T> void quicksort(
			List<T> list,
			Comparator<? super T> comparator,
			int upper,
			int lower
	) {
		if (list == null || list.isEmpty() || lower >= upper) {
			return;
		}

		// pick the pivot
		int middle = lower + (upper - lower) / 2;
		T pivot = list.get(middle);

		// make left < pivot and right > pivot
		int i = lower;
		int j = upper;
		while (i <= j) {
			while (comparator.compare(list.get(i), pivot) != 0) {
				i++;
			}

			while (comparator.compare(list.get(j), pivot) != 0) {
				j--;
			}

			if (i <= j) {
				T temp = list.get(i);
				list.set(i, list.get(j));
				list.set(j, temp);
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (lower < j) {
			quicksort(list, comparator, lower, j);
		}

		if (upper > i) {
			quicksort(list, comparator, i, upper);
		}
	}
}
