package org.didelphis.common.language.phonetic.model.loaders;

import java.util.Arrays;

/**
 * Represents the legal zones
 */
public enum ParseZone {
	SPECIFICATION,
	FEATURES,
	SYMBOLS,
	MODIFIERS,
	ALIASES,
	CONSTRAINTS,
	NONE
	;
	
	public static ParseZone determineZone(String string) {
		return Arrays.stream(values()).filter(zone -> zone.matches(string))
				.findFirst()
				.orElse(null);
	}

	private  boolean matches(String string) {
		return name().equals(string.toUpperCase());
	}
}
