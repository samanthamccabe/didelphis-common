# Didelphis Common
General Didelphis components for language modeling and analysis

## Changelog

### 0.2.0
*??SUMMARY??*

#### Added
- Created `Exceptions` utility class to provide a more fluent API for creating
exceptions when needed

#### Changed
- Started to use Lombok annotations in some code, especially to ease maintenance
of `equals`, `hashCode`, and `toString` methods, as well as logging, and 
delegation
- Began finalizing automated code formatting rules and applied them to large
portions of the project
- Cleaned up old documentation and corrected formatting

### 0.1.0
*Introduced a number of important improvements and fixes, especially for code
coverage and API flexibility*

#### Added
- Created tests for `DiskFileHandler` and others, greatly improving coverage
- Added support for resizeable tables via `ResizeableTable` interface
- Added row and column iterators to `Table` classes
- Created `FeatureType` interface for managing phonetic model values, allowing
them to specify what values are __well-defined__, which are __permitted__, how 
to compute a difference between values, and how to convert feature values to 
Java primitives
- Added `Twin`, a type of `Tuple` where both elemens are of the same type.
`Twin` also implements `List`, allowing it to be more easily used in some cases

#### Changed
- General refactoring of code for clarity and cleanliness
- Unpacked recursive state machines for non-negative cases.
- Switched to use __JUnit5__
- Refactored and tested two-key maps and multimaps
- Greatly refactored and generified `Segment` and `FeatureModel`
- Removed restrictions on type parameters in phonetics classes

#### Fixed
- Corrected inconsistent row & column parameter ordering in `Table` classes

### 0.0.0 (prototype)
*The first steps in building a common library from components of existing tools
in order to reduce redundancy and to create consistency*
- Extracted code from other projects where appropriate
