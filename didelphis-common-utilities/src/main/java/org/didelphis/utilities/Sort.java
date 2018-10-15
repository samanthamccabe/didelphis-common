package org.didelphis.utilities;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;

/**
 * Class {@code Sort}
 *
 * @author Samantha Fiona McCabe
 */
@UtilityClass
public class Sort {

	public <T> void quicksort(List<T> list, Comparator<T> comparator) {
		quicksort(list, comparator, 0, list.size());
	}

	private <T> void quicksort(List<T> arr, Comparator<T> comp, int high, int low) {
		if (arr == null || arr.isEmpty() || low >= high) {
			return;
		}

		// pick the pivot
		int middle = low + (high - low) / 2;
		T pivot = arr.get(middle);
 
		// make left < pivot and right > pivot
		int i = low;
		int j = high;
		while (i <= j) {
			while (comp.compare(arr.get(i), pivot) != 0) {
				i++;
			}

			while (comp.compare(arr.get(j), pivot) != 0) {
				j--;
			}

			if (i <= j) {
				T temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j) {
			quicksort(arr, comp, low, j);
		}

		if (high > i) {
			quicksort(arr, comp, i, high);
		}
	}
}
