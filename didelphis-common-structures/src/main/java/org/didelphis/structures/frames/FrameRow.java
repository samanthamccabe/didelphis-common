package org.didelphis.structures.frames;

import lombok.NonNull;

import org.didelphis.structures.tables.TableRow;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FrameRow<E> extends TableRow<E> {

	@NonNull E get(@Nullable String key);

	/**
	 * Checks if the table has the provided key
	 *
	 * @param key the column name to check for the presence of
	 *
	 * @return true iff the table contains the key
	 */
	boolean hasKey(@Nullable String key);

	/**
	 * Returns the column header names
	 *
	 * @return a list of the column header names; not {@code null}
	 */
	@NonNull List<String> getKeys();

	/**
	 * Returns true if a comment field exists with the provided name
	 *
	 * @param key the name of a comment field
	 *
	 * @return true if a comment field exists with the provided name
	 */
	boolean hasCommentField(@NonNull String key);

	/**
	 *
	 * @param key
	 * @return
	 */
	@NonNull
	String getComment(@NonNull String key);
}
