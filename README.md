# Didelphis Common
**(DRAFT)**

General Didelphis components for language modeling and analysis.

## Language
The **language** components are the heart of the Didelphis projects. It's focus is
phonetic modelling for the purposes of sound change and reconstruction, but it 
also includes a general regular expression engine cabaple of matching not just 
strings, but sequences of arbitrary objects.

### Phonetics
The `phonetic` package provides classes for creating phonetic feature models,
parsing string inputs, and converting them to phonetic sequence objects.

_(TODO)_

### Regular Expressions
The `automata` package provides automata implementations to match, find, and 
replace inputs with both strings and phonetic sequences, as well as providing
classes to create automata for arbitrary sequences types.

_(TODO)_

## Structures
The **structures** component supports many other aspects of the Didelphis project,
with two-key maps, multi-maps, tables, tuples and triples.

### Maps
The `maps` package interfaces and implementations for more complex map types.
Primarily these are two-key maps and multi-maps, which store multiple values per
key. There is also a two-key multi-map which combines the concepts: a map which
stores a collection of values per every key pair.

There are also symmetrical two-key maps, for which the order of the two keys
does not matter: `map.get(a, b)` and `map.get(b, a)` are required to have the
same behavior.

The implenetations take `Supplier` classes as parameters to provide additional
flexibility to the user to choose, for example, the kind of `Collection` used
by a `MultiMap`, whether it is a `HashSet`, `SortedSet`, `ArrayList`, 
`LinkedList`,  or any other type which implements `Collection`.

### Tables
Tables are two-dimensional data-structures with a specific number of rows and
columns, accessed using a pair of indices. `RectangularTable` is the default
implementation.

### Tuples
Tuples are mainly used to support other `structures` classes such as for
representing the key-pair of a two-key map.

There are two types of `Tuple` object:
 + `Twin` represents a `Tuple` whose elements of the same type; as such it is
   also implemented as a list with a fixed size of 2
 + `Couple` represents a `Tuple` whose elements of differing types.

`Triple` should only be used judiciously; for the most part, it is only used to
iterate through the values of a two-key map, or to represent the node-arc-node
structure of a graph.

## IO
The **IO** component contains some basic classes for file IO, but are designed
as interchangeable handlers, using a _strategy pattern_. The intent is to enable
classses which might normally just read from disk to be tested using classpath
resources, or a mock file system.

## Utilities
The **utilities** component provides a number of utility classes for common
tasks such as splitting strings, parsing brackets, null-safe operations,  string
 templates, and an implementation of quicksort.

It also includes a logger implementation that was originally created to support
transpilation, but that effort was ultimately infeasable. This may be removed.

### Templates
The `Templates` utility class was designed to help construct exception messages
using curly braces `{}` similar to SLF4J logger messages.

```
Templates.create()
    .add("Cannot create a table with",
        "negative dimensions! The",
        "dimensions provided were {} by {}")
    .with(width, height);
```
