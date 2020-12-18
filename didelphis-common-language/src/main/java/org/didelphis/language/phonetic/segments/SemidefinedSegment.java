package org.didelphis.language.phonetic.segments;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.didelphis.language.phonetic.features.FeatureArray;

/**
 * Class {@code SemidefinedSegment}
 * <p>
 * An extension of {@code StandardSegment} mainly used to identify segments
 * with a valid primary symbol but where one or more modifier symbols could not
 * be resolved.
 *
 * @param <T> the type of feature data used by the segment's model
 *
 * @since 0.3.3
 */
@Getter
@EqualsAndHashCode (callSuper = true)
public final class SemidefinedSegment extends StandardSegment {

	private final String prefix;
	private final String suffix;

	public SemidefinedSegment(
			String symbol,
			String prefix,
			String suffix,
			FeatureArray featureArray
	) {
		super(symbol, featureArray);
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public String toString() {
		return prefix + " " + super.toString() + " " + suffix;
	}
}
