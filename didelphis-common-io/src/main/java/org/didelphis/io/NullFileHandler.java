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

package org.didelphis.io;

import lombok.NonNull;
import lombok.ToString;

/**
 * Enum {@code NullFileHandler}
 *
 * A simple no-op {@code FileHandler}
 */
@ToString
public enum NullFileHandler implements FileHandler {
	INSTANCE;

	@NonNull
	@Override
	public String read( @NonNull String path) {
		return "";
	}

	@Override
	public void writeString(@NonNull String path, @NonNull String data) {
	}

	@Override
	public boolean validForRead(@NonNull String path) {
		return true;
	}

	@Override
	public boolean validForWrite(@NonNull String path) {
		return true;
	}

}
