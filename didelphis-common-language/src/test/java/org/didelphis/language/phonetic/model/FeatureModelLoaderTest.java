package org.didelphis.language.phonetic.model;

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.io.NullFileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.features.BinaryFeature;
import org.didelphis.language.phonetic.features.DoubleFeature;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureModelLoaderTest extends PhoneticTestBase {

	private static final FeatureModelLoader<Integer> LOADER = IntegerFeature.emptyLoader();

	@Test
	void testConstructorFeatureType() {
		FeatureModelLoader<?> loader1 = new FeatureModelLoader<>(IntegerFeature.INSTANCE);
		FeatureModelLoader<?> loader2 = new FeatureModelLoader<>(BinaryFeature.INSTANCE);
		
		assertEquals(LOADER, loader1);
		assertNotEquals(LOADER, loader2);

		assertEquals(LOADER.hashCode(), loader1.hashCode());
		assertNotEquals(LOADER.hashCode(), loader2.hashCode());
		
		assertEquals(LOADER.toString(), loader1.toString());
		assertNotEquals(LOADER.toString(), loader2.toString());
	}

	@Test
	void testConstructorHandlerAndPath() {
		String path = "AT_hybrid.model";
		FeatureModelLoader<?> loader1 = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				path
		);

		FeatureModelLoader<?> loader2 = new FeatureModelLoader<>(
				DoubleFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				path
		);
		
		assertEquals(loader, loader1);
		assertNotEquals(loader, loader2);

		assertEquals(loader.hashCode(), loader1.hashCode());
		assertNotEquals(loader.hashCode(), loader2.hashCode());

		assertEquals(loader.toString(), loader1.toString());
		assertNotEquals(loader.toString(), loader2.toString());
	}

	@Test
	void testParseTooFewFeatures() {
		List<String> lines = Arrays.asList(
				"FEATURES",
				"foo\tfoo\tbinary",
				"bar\tbar\tbinary",
				"baz\tbaz",
				"SYMBOLS",
				"w\t+\t+"
		);

		assertThrows(ParseException.class,
				() -> new FeatureModelLoader<>(
						IntegerFeature.INSTANCE,
						NullFileHandler.INSTANCE,
						lines
				)
		);
	}

	@Test
	void testParseInvalidFeature() {
		List<String> lines = Arrays.asList(
				"FEATURES",
				"foo\tfoo\tbinary",
				";"
		);

		assertThrows(ParseException.class,
				() -> new FeatureModelLoader<>(
						IntegerFeature.INSTANCE,
						NullFileHandler.INSTANCE,
						lines
				)
		);
	}
	
	@Test
	void testConstructorHandlerAndLines() {
		List<String> lines = Arrays.asList(
				"FEATURES",
				"foo\tfoo\tbinary",
				"bar\tbar\tbinary",
				"SYMBOLS",
				"w\t+\t+",
				"x\t+\t-",
				"y\t-\t+",
				"z\t-\t-",
				"a\t-\t-",
				"\t\t-\t-",
				"MODIFIERS",
				".\t-\t",
				",\t\t-",
				"`\t+\t",
				"^\t\t+",
				"\t\t-\t-"
		);
		
		FeatureModelLoader<Integer> loader1 = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				NullFileHandler.INSTANCE,
				lines
		);

		FeatureSpecification specification = loader1.getSpecification();
		assertEquals(2, specification.size());
		assertEquals(0, specification.getIndex("foo"));
		assertEquals(1, specification.getIndex("bar"));
		
		FeatureMapping<Integer> mapping = loader1.getFeatureMapping();
		assertTrue(mapping.containsKey("w"));
		assertTrue(mapping.containsKey("x"));
		assertTrue(mapping.containsKey("y"));
		assertTrue(mapping.containsKey("z"));
		assertFalse(mapping.containsKey("a"));
		assertFalse(mapping.containsKey("b"));
		assertFalse(mapping.containsKey("c"));
	}
	
	@Test
	void parseConstraint() {
		String entry = "[+lateral] > [-nasal]";
		Constraint<Integer> constraint = loader.parseConstraint(entry);

		FeatureSpecification specification = loader.getSpecification();

		int lateralIndex = specification.getIndex("lateral");
		int nasalIndex = specification.getIndex("nasal");
		assertEquals( 1, (int) constraint.getSource().get(lateralIndex));
		assertEquals(-1, (int) constraint.getTarget().get(nasalIndex));
	}

	@Test
	void getSpecification() {
		assertEquals(0, LOADER.getSpecification().size());
	}

	@Test
	void getFeatureModel() {
		assertEquals(IntegerFeature.INSTANCE, LOADER.getFeatureModel().getFeatureType());
	}

	@Test
	void getFeatureMapping() {
		assertTrue(LOADER.getFeatureMapping().getFeatureMap().isEmpty());
	}
}