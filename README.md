# Didelphis Common
General Didelphis components for language modeling and analysis

## Changelog

### Unreleased

#### Added
- *none*
#### Changed
 - Refactored some constructors for `structures` to use more general types
 - Updated documentation to remove `@date` tags and `created by` lines
#### Deprecated
- *none*
#### Removed
- *none*
#### Fixed
 - Fixed misuse of Lombok annotation `@FieldDefaults` which do not apply to 
   static class members; the code was written at a time when it incorrectly
   *did* affect static members
   [[68]](https://github.com/samanthamccabe/didelphis-common/issues/68)
#### Security
- Changed inner `Cursor` class of `StandardStateMachine` to be static; it was 
	written to access a field of it's containing class, but this can be passed
	as a constructor argument.

### 0.3.0
Substantial changes to several APIs. Greatly improved test coverage. Extensive
additions to the flexibility of state machine code.

#### Added
- Support for capture groups in automata; this required supporting changes
	across much of the automata code
	[[42]](https://github.com/samanthamccabe/didelphis-common/issues/42)
- A simple logger API similar to `Log4j` but which is more easily adapted to
	work with the UI project and the now-abandoned attempts at transpiling to
	Javascript
	[[31]](https://github.com/samanthamccabe/didelphis-common/issues/31)
- A standard regex engine whose behavior is compatible with the standard Java
	`Pattern` library
	[[30]](https://github.com/samanthamccabe/didelphis-common/issues/30)

#### Changed
- *Substantial* and far-reaching changes to the automaton API and its underlying
	implementations. Radically altered the internal functioning of its matching
	code, storage and representation of state machines. Added a large number of 
	tests based on standard regex test suites to identify and correct bugs
	[[24]](https://github.com/samanthamccabe/didelphis-common/issues/24),
	[[46]](https://github.com/samanthamccabe/didelphis-common/issues/46),
	[[55]](https://github.com/samanthamccabe/didelphis-common/issues/55),
	[[57]](https://github.com/samanthamccabe/didelphis-common/issues/57)
- Changed license to GPL 3.0
- Removed uses of `java.util.regex.Pattern` 
	[[45]](https://github.com/samanthamccabe/didelphis-common/issues/45)
- Similarly, the utility class `Exceptions` used reflection. The entire class
	was removed and all uses changed. Instead, exceptions were thrown normally,
	but their messages could be built using the fluid API of `Templates`.
	[[40]](https://github.com/samanthamccabe/didelphis-common/issues/40)
- The `structures` module used reflection in the `Suppliers` utility; this was
	removed, and the required changes were made to the classes which were using
	the removed methods
	[[37]](https://github.com/samanthamccabe/didelphis-common/issues/37)
- Altered `FileHandler` interface to throw `IOException` rather than returning
	`null` as it did originally
	[[21]](https://github.com/samanthamccabe/didelphis-common/issues/21)

#### Fixed
- Fixed errors in parsing `FeatureModel`
	[[61]](https://github.com/samanthamccabe/didelphis-common/issues/61),
	[[64]](https://github.com/samanthamccabe/didelphis-common/issues/64)
- Changed `ClassPathFileHandler` to use its encoding field
	[[48]](https://github.com/samanthamccabe/didelphis-common/issues/48)
- Failing tests in the `didelphis-sca` project revealed un-tested cases in this
	project
	[[35]](https://github.com/samanthamccabe/didelphis-common/issues/35)
- The `Logger` class was found to exhibit some unexpected behavior when only a
	`String` was provided as an argument with no template data
	[[32]](https://github.com/samanthamccabe/didelphis-common/issues/32)
- The expression parser `SequenceParser` was not using special reserved strings
	while splitting inputs, but was not doing this
	[[27]](https://github.com/samanthamccabe/didelphis-common/issues/27)

### 0.2.0
Improved code test coverage and formatting consistency. Migration to use Lombok
for enforcing nullability contracts and for standard `toString`, `equals`, 
*etc.*

#### Added
- Created `Exceptions` utility class to provide a more fluent API for creating
exceptions when needed

#### Changed
- Updated parsing of regular expressions and introducted `Automaton` to 
standardize classes which perform pattern matching
- Started to use Lombok annotations in some code, especially to ease maintenance
	of `equals`, `hashCode`, and `toString` methods, as well as logging,
	delegation and - where appropriate - accessor generation
	[[20]](https://github.com/samanthamccabe/didelphis-common/issues/20)
- Began finalizing automated code formatting rules and applied them to large
portions of the project
- Cleaned up old documentation and corrected formatting

### 0.1.0
Introduced a number of important improvements and fixes, especially for code
coverage and API flexibility

#### Added
- Added `Twin`, a type of `Tuple` where both elements are of the same type.
`Twin` also implements `List`, allowing it to be more easily used in some cases
	[[17]](https://github.com/samanthamccabe/didelphis-common/issues/17)
- Created `FeatureType` interface for managing phonetic model values, allowing
	them to specify what values are __well-defined__, which are __permitted__,
	how to compute a difference between values, and how to convert feature 
	values to Java primitives
	[[15]](https://github.com/samanthamccabe/didelphis-common/issues/15)
- Added row and column iterators to `Table` classes 
	[[12]](https://github.com/samanthamccabe/didelphis-common/issues/12)
- Created tests for `DiskFileHandler` and others, greatly improving coverage
	[[9]](https://github.com/samanthamccabe/didelphis-common/issues/9)
- Added support for resizeable tables via `ResizeableTable` interface
	[[5]](https://github.com/samanthamccabe/didelphis-common/issues/5)
#### Changed
- General refactoring of code for clarity and cleanliness
	[[10]](https://github.com/samanthamccabe/didelphis-common/issues/10)
- Removed restrictions on type parameters in phonetics classes 
	[[6]](https://github.com/samanthamccabe/didelphis-common/issues/6)
- Refactored and tested two-key maps and multimaps 
	[[1]](https://github.com/samanthamccabe/didelphis-common/issues/1)
- Unpacked recursive state machines for non-negative cases.
- Switched to use __JUnit5__
- Greatly refactored and generified `Segment` and `FeatureModel`

#### Fixed
- Corrected inconsistent row & column parameter ordering in `Table` classes
 [[3]](https://github.com/samanthamccabe/didelphis-common/issues/3)

### 0.0.0 (prototype)
The first steps in building a common library from components of existing tools
in order to reduce redundancy and to create consistency
- Extracted code from other projects where appropriate

## Maintaining the Changelog

Use the following for guidance:
[Keep a Changelog](https://keepachangelog.com/en/0.3.0)

Version logs should be broken into the following sections where possible:
 - Added
 - Changed
 - Deprecated
 - Removed
 - Fixed
 - Security
 
Keep an *Unreleased* version section for changes which are awaiting the next
release. Try to add any changes to the top of the log section as you make them,
and attach ticket links where possible.