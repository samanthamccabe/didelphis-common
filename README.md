# Didelphis Common
General Didelphis components for language modeling and analysis

## Changelog

### 0.2.0
*??SUMMARY??*

#### Added
- Created `Exceptions` utility class to provide a more fluent API for creating
exceptions when needed

#### Changed
- Updated parsing of regular expressions and introducted `Automaton` to 
standardize classes which perform pattern matching
- Started to use Lombok annotations in some code, especially to ease maintenance
of `equals`, `hashCode`, and `toString` methods, as well as logging, delegation,
and where appropriate, accessor generation
	[[didelphis-common-20]](https://github.com/samanthamccabe/didelphis-common/issues/20)
- Began finalizing automated code formatting rules and applied them to large
portions of the project
- Cleaned up old documentation and corrected formatting

### 0.1.0
*Introduced a number of important improvements and fixes, especially for code
coverage and API flexibility*

#### Added
- Created tests for `DiskFileHandler` and others, greatly improving coverage
	[[didelphis-common-9]](https://github.com/samanthamccabe/didelphis-common/issues/9)
- Added support for resizeable tables via `ResizeableTable` interface
	[[didelphis-common-5]](https://github.com/samanthamccabe/didelphis-common/issues/5)
- Added row and column iterators to `Table` classes 
	[[didelphis-common-12]](https://github.com/samanthamccabe/didelphis-common/issues/12)
- Created `FeatureType` interface for managing phonetic model values, allowing
them to specify what values are __well-defined__, which are __permitted__, how 
to compute a difference between values, and how to convert feature values to 
Java primitives
	[[didelphis-common-15]](https://github.com/samanthamccabe/didelphis-common/issues/15)
- Added `Twin`, a type of `Tuple` where both elemens are of the same type.
`Twin` also implements `List`, allowing it to be more easily used in some cases
	[[didelphis-common-17]](https://github.com/samanthamccabe/didelphis-common/issues/17)
#### Changed
- General refactoring of code for clarity and cleanliness
	[[didelphis-common-10]](https://github.com/samanthamccabe/didelphis-common/issues/10)
- Unpacked recursive state machines for non-negative cases.
- Switched to use __JUnit5__
- Refactored and tested two-key maps and multimaps 
	[[didelphis-common-1]](https://github.com/samanthamccabe/didelphis-common/issues/1)
- Greatly refactored and generified `Segment` and `FeatureModel`
- Removed restrictions on type parameters in phonetics classes 
	[[didelphis-common-6]](https://github.com/samanthamccabe/didelphis-common/issues/6)

#### Fixed
- Corrected inconsistent row & column parameter ordering in `Table` classes
 [[didelphis-common-3]](https://github.com/samanthamccabe/didelphis-common/issues/3)

### 0.0.0 (prototype)
*The first steps in building a common library from components of existing tools
in order to reduce redundancy and to create consistency*
- Extracted code from other projects where appropriate
