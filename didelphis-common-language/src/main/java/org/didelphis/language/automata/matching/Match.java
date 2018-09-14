/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.automata.matching;

import org.jetbrains.annotations.Nullable;

/**
 * Interface {@code Match}
 *
 * Like {@link java.util.regex.MatchResult} but parameterized
 * 
 * @author Samantha Fiona McCabe
 * @date 10/21/17
 */
public interface Match<S> {

	/**
	 * Returns the start index of the match.
	 *
	 * @return  The index of the first character matched
	 *
	 * @throws  IllegalStateException
	 *          If no match has yet been attempted,
	 *          or if the previous match operation failed
	 */
	int start();

	/**
	 * Returns the start index of the subsequence captured by the given group
	 * during this match.
	 *
	 * <p> <a href="Pattern.html#cg">Capturing groups</a> are indexed from left
	 * to right, starting at one.  Group zero denotes the entire pattern, so
	 * the expression <i>m.</i><tt>start(0)</tt> is equivalent to
	 * <i>m.</i><tt>start()</tt>.  </p>
	 *
	 * @param  group
	 *         The index of a capturing group in this matcher's pattern
	 *
	 * @return  The index of the first character captured by the group,
	 *          or <tt>-1</tt> if the match was successful but the group
	 *          itself did not match anything
	 *
	 * @throws  IllegalStateException
	 *          If no match has yet been attempted,
	 *          or if the previous match operation failed
	 *
	 * @throws  IndexOutOfBoundsException
	 *          If there is no capturing group in the pattern
	 *          with the given index
	 */
	int start(int group);

	/**
	 * Returns the offset after the last character matched.
	 *
	 * @return  The offset after the last character matched
	 *
	 * @throws  IllegalStateException
	 *          If no match has yet been attempted,
	 *          or if the previous match operation failed
	 */
	int end();


	/**
	 * Returns the offset after the last character of the subsequence
	 * captured by the given group during this match.
	 *
	 * <p> <a href="Pattern.html#cg">Capturing groups</a> are indexed from left
	 * to right, starting at one.  Group zero denotes the entire pattern, so
	 * the expression <i>m.</i><tt>end(0)</tt> is equivalent to
	 * <i>m.</i><tt>end()</tt>.  </p>
	 *
	 * @param  group
	 *         The index of a capturing group in this matcher's pattern
	 *
	 * @return  The offset after the last character captured by the group,
	 *          or <tt>-1</tt> if the match was successful
	 *          but the group itself did not match anything
	 *
	 * @throws  IllegalStateException
	 *          If no match has yet been attempted,
	 *          or if the previous match operation failed
	 *
	 * @throws  IndexOutOfBoundsException
	 *          If there is no capturing group in the pattern
	 *          with the given index
	 */
	int end(int group);

	/**
	 * Returns the input subsequence captured by the given group during the
	 * previous match operation.
	 *
	 * <p> For a matcher <i>m</i>, input sequence <i>s</i>, and group index
	 * <i>g</i>, the expressions <i>m.</i><tt>group(</tt><i>g</i><tt>)</tt> and
	 * <i>s.</i><tt>substring(</tt><i>m.</i><tt>start(</tt><i>g</i><tt>),</tt>&nbsp;<i>m.</i><tt>end(</tt><i>g</i><tt>))</tt>
	 * are equivalent.  </p>
	 *
	 * <p> <a href="Pattern.html#cg">Capturing groups</a> are indexed from left
	 * to right, starting at one.  Group zero denotes the entire pattern, so
	 * the expression <tt>m.group(0)</tt> is equivalent to <tt>m.group()</tt>.
	 * </p>
	 *
	 * <p> If the match was successful but the group specified failed to match
	 * any part of the input sequence, then <tt>null</tt> is returned. Note
	 * that some groups, for example <tt>(a*)</tt>, match the empty string.
	 * This method will return the empty string when such a group successfully
	 * matches the empty string in the input.  </p>
	 *
	 * @param  group
	 *         The index of a capturing group in this matcher's pattern
	 *
	 * @return  The (possibly empty) subsequence captured by the group
	 *          during the previous match, or <tt>null</tt> if the group
	 *          failed to match part of the input
	 *
	 * @throws  IllegalStateException
	 *          If no match has yet been attempted,
	 *          or if the previous match operation failed
	 *
	 * @throws  IndexOutOfBoundsException
	 *          If there is no capturing group in the pattern
	 *          with the given index
	 */
	@Nullable S group(int group);

	/**
	 * Returns the number of capturing groups in this match result's pattern.
	 *
	 * <p> Group zero denotes the entire pattern by convention. It is not
	 * included in this count.
	 *
	 * <p> Any non-negative integer smaller than or equal to the value
	 * returned by this method is guaranteed to be a valid group index for
	 * this matcher.  </p>
	 *
	 * @return The number of capturing groups in this matcher's pattern
	 */
	int groupCount();
}