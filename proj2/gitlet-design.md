# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Main

This is the entry point to our program. It takes in arguments from the command line and based on the command (the first element of the `args` array) calls the corresponding command in `Repository` which will actually execute the logic of the command. It also validates  the arguments by `validateNumArgs()` based on the command to ensure that enough arguments were passed in.

#### Fields

This class has no fields and hence no associated state: it simply validates arguments and defers the execution to the `Repository` class.

### Utils

This class contains helpful utility methods to read/write objects or `String` contents from/to files, as well as reporting errors when they occur.

This is a staff-provided and PNH written class, so we leave the actual implementation as magic and simply read the helpful javadoc comments above each method to give us an idea of whether or not it’ll be useful for us.

#### Fields

Only some `private` fields to aid in the magic.

// about initial Command

```java
        // if workingFiles equal files of current commit OR files of old commit
        // current commit: do nothing
        // old commit: change active branch to point to old commit

        // if workingFiles NOT equal files of current commit
        // we should trace back to old commit AS current commit

        // if workingFiles NOT equal files of current commit AND files of old commit
        // addition for staging area
```




## Algorithms

This is where you tell us how your code works. For each class, include a high-level description of the methods in that class.

## Persistence

