package org.didelphis.structures.frames;

import lombok.NonNull;

import org.didelphis.structures.tables.TableColumn;

public interface FrameColumn<E> extends TableColumn<E> {

	@NonNull String getKey();

}
