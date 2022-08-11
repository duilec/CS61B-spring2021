# CS61B

## introduce

This is my solution about homework, lab and project in CS61B spring2021

## course website

- [CS61B spring2021](https://sp21.datastructur.es/)

## course progress

- homework
  - [x] hw0
  - [ ] hw2
  - [ ] hw3
- lab
  - [x] lab1
  - [x] lab2
  - [ ] lab3
  - [ ] ...
- project
  - [x] proj1
  - [ ] proj2
  - [ ] proj3
  - [ ] ...

## homeworks

### hw0

time: *2022.08.09*

- I only write a version of "for" in `GetMax`

## labs

### lab1

time: *2022.08.08*

- gradescope token in CS61B spring2021: `MB7ZPY`

  **FROM** [CS61B gradescope for students that audit the course](https://www.reddit.com/r/berkeley/comments/pihntt/cs61b_gradescope_for_students_that_audit_the/)

- you should add **your repository(origin)** and **skeleton** as remote repositories.

  ```bash
  $ git remote -v
  origin  https://github.com/duilec/CS61B.git (fetch)
  origin  https://github.com/duilec/CS61B.git (push)
  skeleton        https://github.com/Berkeley-CS61B/skeleton-sp21.git (fetch)
  skeleton        https://github.com/Berkeley-CS61B/skeleton-sp21.git (push)
  ```

- submit

  ```bash
  # normal
  git add xxxn/*
  git commit -m "done with xxx"
  git push origin main

  # eg. lab1
  git add lab1/*
  git commit -m "done with Collatz"
  git push origin main
  ```

### lab2

time: *2022.08.11*

- lab2setup
  - you may move directory of  `javalib`  from `...\library-sp21\library-sp21\javalib`to  `...\library-sp21\javalib` (i.e. you should **delete a  `library-sp21`**), so `masterpom.xml` could be used. 
  - you also could change `Project Strcuture` ,`Maven` and `Compiler/Java Comiler` by using google or bing.
- debug
  - stepping into
    the *step into* from the previous exercise shows the literal next step of the program
  - stepping over
    the *step over* button allows us to complete a function call without showing the function executing.
  - stepping out
    If you find yourself accidentally stepping into one of these two functions, use the *step out* to escape
- math
  `0`, `1` are not prime

## projects

### proj1

time: *2022.08.09*

- keeping patient when reading English text

- a normal mistake lead to **infinite loop**:

  ```java
  for (int k = 0; k < tiles.length; i++){
    ...
  }
  ```

  initialize variable is  `k = 0`, condition is `k < tiles.length`, **but increment is `i++` not `k++`** lead to infinite loop. So, we should carefully **change variable one by one**, **when using a copy**.