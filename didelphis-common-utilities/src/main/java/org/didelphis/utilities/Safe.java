package org.didelphis.utilities;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Safe {

	@NonNull
	public String toString(@Nullable Object object) {
		return (object == null ? "null" : object.toString());
	}

	public int hashCode(@Nullable Object object) {
		return (object == null ? 0 : object.hashCode());
	}
}
