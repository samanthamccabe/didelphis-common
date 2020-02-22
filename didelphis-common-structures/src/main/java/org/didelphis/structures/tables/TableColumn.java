package org.didelphis.structures.tables;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TableColumn<E> extends List<E> {

	/**
	 * Returns the index of the column in the Frame to which it belongs
	 * @return the index of this row in the Frame to which it belongs
	 */
	int getIndex();

	/**
	 * Returns the table to which this column belongs
	 * @return the table that this column comes from; not null
	 */
	@NotNull
	Table<E, TableRow<E>, TableColumn<E>> getTable();
}
