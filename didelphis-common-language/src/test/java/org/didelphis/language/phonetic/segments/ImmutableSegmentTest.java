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

package org.didelphis.language.phonetic.segments;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * Class {@code ImmutableSegmentTest}
 *
 * @since 0.1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ImmutableSegmentTest extends PhoneticTestBase {

	Segment<Integer> segment1 = factory.toSegment("a");
	Segment<Integer> segment2 = factory.toSegment("b");

	Segment<Integer> segment3 = factory.toSegment("a");
	Segment<Integer> segment4 = factory.toSegment("b");

	Segment<Integer> immutable1 = new ImmutableSegment<>(segment1);
	Segment<Integer> immutable2 = new ImmutableSegment<>(segment2);

	Segment<Integer> immutable3 = new ImmutableSegment<>(segment3);
	Segment<Integer> immutable4 = new ImmutableSegment<>(segment4);

	@Test
	void testEmptyConstructor() {
		ImmutableSegment<Integer> segment = new ImmutableSegment<>(
				segment1.getSymbol(),
				segment2.getFeatureModel()
		);
		assertEquals(segment1.getSymbol(), segment.getSymbol());
	}

	@Test
	void testStandardConstructor() {
		ImmutableSegment<Integer> segment = new ImmutableSegment<>(
				segment1.getSymbol(),
				segment2.getFeatures()
		);
		assertEquals(segment1.getSymbol(), segment.getSymbol());
	}

	@Test
	void alter() {
		assertFalse(immutable1.alter(segment1));
		assertFalse(immutable1.alter(segment2));

		assertFalse(immutable2.alter(segment1));
		assertFalse(immutable2.alter(segment2));

		assertFalse(immutable1.alter(immutable3));
		assertFalse(immutable1.alter(immutable4));
	}

	@Test
	void equals() {
		assertNotEquals(immutable1, segment1);
		assertNotEquals(immutable2, segment2);
		assertNotEquals(immutable1, immutable2);

		assertEquals(immutable1, immutable3);
		assertEquals(immutable2, immutable4);
	}

	@Test
	void testHashCode() {
		assertNotEquals(segment1.hashCode(), immutable1.hashCode());
		assertNotEquals(segment2.hashCode(), immutable2.hashCode());
		assertNotEquals(immutable1.hashCode(), immutable2.hashCode());
		
		assertEquals(immutable1.hashCode(), immutable3.hashCode());
		assertEquals(immutable2.hashCode(), immutable4.hashCode());
	}

	@Test
	void testToString() {
		assertNotEquals(segment1.toString(), immutable1.toString());
		assertNotEquals(segment2.toString(), immutable2.toString());
		assertNotEquals(immutable1.toString(), immutable2.toString());

		assertEquals(immutable1.toString(), immutable3.toString());
		assertEquals(immutable2.toString(), immutable4.toString());
	}

}
