# Didelphis Common
General Didelphis components for language modeling and analysis

## IO
s

## Language
s

### Regular Expressions
s

### Phonetics
s

## Structures
s

### Maps
s 

### Tables
s

### Tuples
s

## Utilities
s

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
