package org.didelphis.language.phonetic.model;

import lombok.experimental.UtilityClass;

import org.didelphis.language.automata.Regex;

@UtilityClass
public class ModelConstants {

	final String VALUE = "(-?\\d|[A-Z]+)";
	final String NAME  = "(\\w+)";
	final String ASSN  = "([=:><])";

	final Regex BINARY_PATTERN  = new Regex("([+−-])" + NAME);
	final Regex VALUE_PATTERN   = new Regex(VALUE + ASSN + NAME);
	final Regex FEATURE_PATTERN = new Regex("[,;]\\s*|\\s+");
	final Regex BRACKET_PATTERN = new Regex("\\[((?:[^]])+)]");
	final Regex DASH = new Regex("[͜-͢]");

	final Regex FEATURES_PATTERN = new Regex("(\\w+)(\\s+(\\w*))?");
	final Regex TRANSFORM        = new Regex("\\s*>\\s*");
	final Regex BRACKETS         = new Regex("[\\[\\]]");
	final Regex EQUALS           = new Regex("\\s*=\\s*");
	final Regex IMPORT           = new Regex("import\\s+['\"](.+?)['\"]", true);
	final Regex COMMENT_PATTERN  = new Regex("\\s*%.*");
	final Regex SYMBOL_PATTERN   = new Regex("([^\t]+)\t(.*)");
	final Regex TAB_PATTERN      = new Regex("\t");
	final Regex DOTTED_CIRCLE    = new Regex("◌");
	final Regex PARENT_PATH      = new Regex("^(.*[\\\\/])?[^\\\\/]+$");
}
