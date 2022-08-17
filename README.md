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
  - [x] lab3
  - [ ] ...
- project
  - [x] proj0
  - [ ] proj1
  - [ ] proj2
  - [ ] ...

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

### lab3

time: *2022.08.13*

- debug: 
  - resume
    Note that every time you click resume, the code is running (as if you pressed step-over a bunch of times) until it **gets back to the breakpoint again**.
    
  - condition breakpoint
    **Right-click on your breakpoint and you’ll see a box pop up that says “Condition:”**. In the box, type `L.size() == 12`.
    ![image-20220813114451968](https://s2.loli.net/2022/08/13/7sw8YPKHFgaIrDx.png)
    
  - execution breakpoint
    we can stop the code and visualize what’s going on **when your code crashes**.
    two step

    1. click “Run -> View Breakpoints”, you should see a window like this pop up.
    
    2. Click on the checkbox on the left that says “any exception” and then click on that says “Condition:” and in the window and enter exactly:
    
       ```java
       // eg: 
       this instanceof java.lang.ArrayIndexOutOfBoundsException
       ```

  - **don't need main() in test of Junit**
  - log
    Often it is useful to “log” rather than print out the function calls made by randomized tests.
For more on this, complete the project 1 extra credit assignment.
- use **randomized** tests
  Use Fixed Data Instead of Randomized Data Avoid randomized data as it can lead to toggling tests which can be hard to debug and omit error messages that make tracing the error back to the code harder. They will create highly reproducible tests, which are **easy to debug and create error messages** that can be easily traced back to the relevant line of code. 
  See [this thread](https://news.ycombinator.com/item?id=24349522) for a debate on this issue.

## projects

### proj0

start time: *2022.08.09*
end time: *2022.08.09*

- keeping patient when reading English text

- a normal mistake lead to **infinite loop**:

  ```java
  for (int k = 0; k < tiles.length; i++){
    ...
  }
  ```

  initialize variable is  `k = 0`, condition is `k < tiles.length`, **but increment is `i++` not `k++`** lead to infinite loop. So, we should carefully **change variable one by one**, **when using a copy**.
### proj1

start time: *2022.08.13*
end time: *2022.08.xx*

- package
  - When **creating a package**, we specify that code is part of a package by specifying the package name at the top of the file using the `package` keyword.
  - Typically, package names are the internet address of the entity writing the code, **but backwards**. For example, the JUnit library is hosted at `junit.org`, so the package is called `org.junit`.
  
- `LinkedListDeque` and `ArrayDeque`(proj1 checkpoint)
  
  - before doing proj1 checkpoint
    **Strongly recommend**: See the [project 1 demo slides](https://docs.google.com/presentation/d/1XBJOht0xWz1tEvLuvOL4lOIaY0NSfArXAvqgkrx0zpc/edit#slide=id.g1094ff4355_0_450) for more details.
    
- if you need more token to test in gradescope, you could create new account
  
  - note:
    
    - `mod %`
    
      `-1 % 32 == -1` in java, `-1 % 32 == 31` in python3
    
    - add first
       when adding first from `0` to `3`, then, the list is `3->2->1->0`, the `3` is first
    
- tips about `add` and `remove`
  
  - if `resize()` called by `addFirst/Last`, copy to all old items in **middle part** (from "size/2" to "size/2 + size - 1"). Otherwise, `resize()` called by `removeFirst/Last`, copy to all old items **from index of first**
  - keeping circular deque, **the index of first after `nextFirst`** and **the index of last before `nextLast`**
  
- `MaxArrayDeque`

  - You’ll likely be creating multiple `Comparator<T>` classes to test your code(i.e. we need build different comparator to test in `MaxArrayDeque`)
  - note
    - we use method of comparator to compare
    - we should have same name and signature when overriding.
      so, we can't use `public double compare(Double d1, Double d2)`but only can use `public int compare(Double d1, Double d2)`

## homework

### hw0

time: *2022.08.09*

- I only write a version of "for" in `GetMax`