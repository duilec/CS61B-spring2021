# Gitlet Design Document

**Name**: Huang Jinhong

## Classes and Data Structures

Include here any class definitions. For each class list the instance variables and static variables (if any). Include a ***brief description\*** of each variable and its purpose in the class. Your explanations in this section should be as concise as possible. Leave the full explanation to the following sections. You may cut this section short if you find your document is too wordy.

### Main

This is the entry point to our program. It takes in arguments from the command line and based on the command (the first element of the `args` array) calls the corresponding command in `Repository` which will actually execute the logic of the command. It also validates the arguments by `validateInitAndOperands()` based on the command to ensure that Persistence was be created,  enough arguments were passed(`validateOperands()`) in and arguments are right(`matchFileName()`, `matchMessage()`, `matchTwoLines()`, `matchBranchName()`, `matchCommitID()`, `matchRemoteName()`, `matchRemoteDirectory()`).

#### Fields

This class has no fields and hence no associated state: it simply validates arguments and defers the execution to the `Repository` class.

### Utils

This class contains helpful utility methods to read/write objects or `String` contents from/to files, as well as reporting errors when they occur.

This is a staff-provided and PNH written class, so we leave the actual implementation as magic and simply read the helpful javadoc comments above each method to give us an idea of whether or not it’ll be useful for us.

Added three methods: 

- `public static void printError(String msg)`, which prints message of error 

- `public static void printErrorWithExit(String msg)`, which prints message of error and exits program

- `static boolean unrestrictedDelete(File file)`, 

  Deletes the file named `FILE` if it exists and is not a directory. Returns `true` if `FILE` was deleted, and `false` otherwise.

#### Fields

Only some `private` fields to aid in the magic.

### MyUtils



#### Fields



### Blob



#### Fields



### Commit



#### Fields



### Repository



#### Fields



### Pointer



#### Fields



### Remote



#### Fields



### Dumpable

An interface describing dumpable objects.

(I don't use it)

#### Fields

NOT

### DumpObj

A debugging class whose main program may be invoked as follows: `java gitlet.DumpObj FILE`

(I don't use it)

#### Fields

NOT

### GitletException

General exception indicating a Gitlet error.  For fatal errors, the result of .`getMessage()` is the error message to be printed.

(I don't use it)

#### Fields

NOT

## Algorithms

This is where you tell us how your code works. For each class, include a high-level description of the methods in that class. That is, do not include a line-by-line breakdown of your code, but something you would write in a javadoc comment above a method, ***including any edge cases you are accounting for\***. We have read the project spec too, so make sure you do not repeat or rephrase what is stated there. This should be a description of how your code accomplishes what is stated in the spec.

The length of this section depends on the complexity of the task and the complexity of your design. However, simple explanations are preferred. Here are some formatting tips:

- For complex tasks, like determining merge conflicts, we recommend that you split the task into parts. Describe your algorithm for each part in a separate section. Start with the simplest component and build up your design, one piece at a time. For example, your algorithms section for Merge Conflicts could have sections for:
  - Checking if a merge is necessary.
  - Determining which files (if any) have a conflict.
  - Representing the conflict in the file.
- Try to clearly mark titles or names of classes with white space or some other symbols.

### Repository

- graph pre-order travel
  Using graph pre-order travel in  `markBranch()`, Travels a branch from commit of branch to initial commit.
  Using recursion.
- list travel
  Using list travel in  `printCommitLogInActiveBranch()`, Travels the active branch from commit of branch to initial commit.
  Using recursion.

## Persistence

Describe your strategy for ensuring that you don’t lose the state of your program across multiple runs. Here are some tips for writing this section:

- This section should be structured as a list of all the times you will need to record the state of the program or files. For each case, you must prove that your design ensures correct behavior. For example, explain how you intend to make sure that after we call java gitlet.Main add wug.txt, on the next execution of java gitlet.Main commit -m “modify wug.txt”, the correct commit will be made.
- A good strategy for reasoning about persistence is to identify which pieces of data are needed across multiple calls to Gitlet. Then, prove that the data remains consistent for all future calls.
- This section should also include a description of your .gitlet directory and any files or subdirectories you intend on including there.

The directory structure looks like this:

**fix directory structure look(don't use `space` but use `Tab`) 22.10.07**

```java
CWD                             <==== Whatever the current working directory is.
└── .gitlet                     <==== All persistant data is stored within here
    ├── HEAD                    <==== A single Pointer(HEAD) instance stored to a file
    ├── addition                <==== All the added file are stored in this directory
  	│   ├── file1                 <==== A single file
    │   ├── file2
    │   ├── ...
    │   └── fileN
    ├── blobs                   <==== All the dirs of blobs are stored in this directory
  	│   ├── blobDir1             <==== A directory of blobs
    │   │   ├── blob1             <==== A single Bolb instance stored to a file
    │   │   ├── ...
    │   │   └── blobN
    │   ├── blobDir2
    │   │   ├── blob1
    │   │   ├── ...
    │   │   └── blobN
    │   ├── ...
    │   └── blobDirN
    │       ├── blob1
    │       ├── ...
    │       └── blobN
    ├── branch                   <==== All the dir of blobs are stored in this directory
  	│   ├── branch1               <==== A single Pointer(Branch) stored to a file
    │   ├── branch2
    │   ├── ...
    │   └── branchN
    ├── commits                 <==== All the commits are stored in this directory
  	│   ├── commit1              <==== A single Commit instance stored to a file
    │   ├── commit2
    │   ├── ...
    │   └── commitN
    ├── remote                  <==== All the remotes are stored in this directory
  	│   ├── remote1              <==== A single Remote instance stored to a file
    │   ├── remote2
    │   ├── ...
    │   └── remoteN
    └── removed                 <==== All the added file are stored in this directory
  			├── file1                 <==== A single file
    		├── file2
       	├── ...
       	└── fileN
/**
* Does required filesystem operations to allow for persistence.
* (creates any necessary folders or files)
*
* .gitlet/ -- top level folder for all persistent data in proj2 folder
*    - commits/ -- folder containing all of the persistent object for commits
*    - blobs/ -- folder containing all of the persistent folder for folder of blobs
*          - 00/ -- folders containing all of the persistent object for blobs
*          - 01/
*          - ../(two characters of hex)
*          - ff/
*    - addition/ -- folder containing all of the staging file for addition
*    - removed/ -- folder containing all of the staging file for removed
*    - branch/ -- folder containing all of the persistent object for branch
*    - HEAD -- file containing the persistent object for head
*/
```

The `Repository` will set up all persistence. It will:

1. Create the `.gitlet` folder
2. Create the `commits` folder
3. Create the `addition` folder
4. Create the `removed` folder
5. Create the `branch` folder
6. Create the `HEAD`  of instance of `Pointer`
7. Create the `initcommit`  of instance of `Commit` in the `commits` folder

## Example

To illustrate all this, we’ve created a [sample design document](https://sp21.datastructur.es/materials/proj/proj2/capers-example.html) for the Capers lab.

