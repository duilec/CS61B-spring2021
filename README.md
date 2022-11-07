# CS61B

## introduce

This is my solution about homework, lab and project in CS61B spring2021

## course website

- [CS61B spring2021](https://sp21.datastructur.es/)
- [CS61B spring2018](https://sp18.datastructur.es/)

## course progress

- homework
  - [x] hw0
  - [ ] hw2 (I skip it)
  - [ ] hw3 (I skip it)
- lab
  - [x] lab1
  - [x] lab2
  - [x] lab3
  - [x] lab4
  - [ ] lab5 (I skip it)
  - [x] lab6
  - [x] lab7
  - [x] lab8
  - [x] [lab11](https://github.com/duilec/CS61B-spring2018/tree/main/lab11/lab11/graphs) (graph exercises in 18sp)
  - [x] lab12
  - [x] lab13
- project
  - [x] proj0
  - [x] proj1
  - [x] proj1EC
  - [x] proj2
  - [x] proj2EC(compete `status` & `remote` but **NOT**  `diff` )
  - [x] proj3 (done with phase1 and primary of phase2 but **NOT** secondary of phase2 and network)
  - [ ] proj3EC(I skip it)

## helper link

- [cs61b staff](https://github.com/Berkeley-CS61B)

## labs

### lab1

time: *2022.08.08*

- gradescope token 

  - CS61B spring2022: `743W56` **but not using**
  - CS61B spring2021: `MB7ZPY` or `BP25V6` **FROM** [CS61B gradescope for students that audit the course](https://www.reddit.com/r/berkeley/comments/pihntt/cs61b_gradescope_for_students_that_audit_the/)
  - CS61B summer(spring)2018:`MNXYKX`

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

### lab4

time: *2022.08.19*

I directly `git copy` entire `skeleton`, so I can't do lab4A and lab4B. I learned some knowledge of git in [The Missing Semester of Your CS Education](https://missing.csail.mit.edu/) and I refer it to you.

- but, you can learn how to use `git checkout` to **travel old version** FROM [Sarah’s Git Guide](https://sp19.datastructur.es/materials/guides/using-git). 
  NOTE: **don't directly copy and paste old `panorama`**, you must `git checkout` with **`panorama and path`**. otherwise, you will meet `detached head`(it is a usual error, but can be fixed FROM [Git WTFS](https://sp19.datastructur.es/materials/guides/git-wtfs) )
- If you worried about error, you can create a new repository in your github.

Debugging

- `Integer`is object in java

- `==` differ `.equals()` in java
  - `==` compare bits, either `data of basic type` or `address of object`
  - `.equals()` compare `content of object`

Setting environment variable in wsl2 (*2022.8.27*)

```bash
#open .bashrc file (in bash)
vim ~/.bashrc
  
#setting environment variable in the end of file
export REPO_DIR=/mnt/c/Users/.../sp-s21(your repo)
  
#let setting useful (in bash)
source ~/.bashrc
```

[maybe useful link to setting environment variable in window](https://fa20.datastructur.es/materials/lab/lab2setup/lab2setupWindows.html)

### lab5

you could skip it, because you know you do proj1 by yourself and you not have TAs.

### lab6

time: *2022.08.26*

NOTE: **read guide of lab one by one!**

`writeStory()`

- `writeContents()` different with `writeObject()`

[serialization](https://en.wikipedia.org/wiki/Serialization)

- To enable this feature(serialization) for a given class in Java, this simply involves **implementing the `java.io.Serializable` interface**.
  **eg.** `public class Dog implements Serializable{...}`

### lab7

time:  *2022.09.01*

if you **stuck** you can refer it: BST code from [our optional textbook](https://algs4.cs.princeton.edu/32bst/BST.java.html).

NOTE: you can travel a tree without using `return` but using `=`  to assign in some part

### lab8

time:  *2022.09.05*

iterator

I choose classic approach of `LinkedList`  to implement to `MyHashMap`

- **factory method**
  depend on polymorphism and inheritance, 
  eg. we can use different type of bucket by a factory method `protected Collection<Node> createBucket()`

- `protected`

  // todo

- **you can just create a `LinkedList` to iterate**

  - you also can use some counters to record and getting outputs, but need greater patience and more tricks.

- you don't create an iterator of `b` just using `b`(object of `MyHashMap`)

  - when you iterate `b`, `b` will  **automatically** calls `iterator()` NOT need user manually calls.

  ```java
  // Error
  for (String key : b.iterator()){
      System.out.println(key);
  }
  
  // Right
  for (String key : b){
      System.out.println(key);
  }
  ```

### lab11

time: *2022.09.16 ~ 2022.09.17*

this is some exercises about graph

- DFS

  - solution recursion + *for* loop 
  - you can using **recursion** (NOT tail recursion)to instead of **stack**(FIFO), because both equal.
  - DFS like *preorder* travel

- BFS

  - solution queue + *for* loop 
  - you can use any type of queue, I choose `ArrayDeque`
  - in queue
    - `add()` equal `addLast()`
    - `remove()` equal `removeFirst()`
  - BFS like level *levelOrder* travel

- detect Cycle

  - only find a cycle with 4 nodes in my solution

  - about **check**

    ```java
    // you should use "else if" to check w to find cycle
    // you couldn't use "if" to check w to find cycle.
    // because you can't mark w, then, immediately check it.
    // otherwise, when tracebacking to second node you will immediately return.
    // you only do one thing(mark or check) when visiting a w.
    ```

- A*

  - Dijkstra's like BFS, but consider **distance from source to target**, 
    each time remove vertex of minimum distance in fringe
  - A* also like BFS (Dijkstra's) , but consider **sum of distance from source to vertex and distance from vertex to target**, each time remove vertex of minimum sum of distance in fringe

## projects

**I don't care about the style in CS61B**

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
end time: *2022.08.19*

I download `music.mid` FROM [bitmidi](https://bitmidi.com/)

- package
  - When **creating a package**, we specify that code is part of a package by specifying the package name at the top of the file using the `package` keyword.
  - Typically, package names are the internet address of the entity writing the code, **but backwards**. For example, the JUnit library is hosted at `junit.org`, so the package is called `org.junit`.
  
- `LinkedListDeque` and `ArrayDeque`(proj1 checkpoint)
  
  - before doing proj1 checkpoint
    **Strongly recommend**: See the [project 1 demo slides](https://docs.google.com/presentation/d/1XBJOht0xWz1tEvLuvOL4lOIaY0NSfArXAvqgkrx0zpc/edit#slide=id.g1094ff4355_0_450) for more details.
  
- if you need more token to test in gradescope, you could create new account
  
  - note:
    
    - `mod %`
    
      `-1 % 32 == -1` , `Math.floorMod(-1, 32) == 31`in java, `-1 % 32 == 31` in python3
    
    - add first
       when adding first from `0` to `3`, then, the list is `3->2->1->0`, the `3` is first
  
- tips about `add` and `remove`
  
  - if `resize()` called by `addFirst/Last`, copy to all old items in **middle part** (from "size/2" to "size/2 + size - 1"). Otherwise, `resize()` called by `removeFirst/Last`, copy to all old items **from index of first**
  - keeping circular deque, **the index of first after `nextFirst`** and **the index of last before `nextLast`**
  
- `iterator()` and `equals()`

  - use `equals` instead of `==` when comparing of two objects
  - **a class can implement one or more interfaces**.

- `MaxArrayDeque`

  - You’ll likely be creating multiple `Comparator<T>` classes to test your code(i.e. we need build different comparator to test in `MaxArrayDeque`)
  - note
    - Construct an instance of class by using `new ClassName()`, when the class **not pass argument to constructor**
    - we use method of comparator to compare
    - we should have same name and signature when overriding.
      so, we can't use `public double compare(Double d1, Double d2)`but we can use `public int compare(Double d1, Double d2)`
  
- `GuitarString` and `GuitarHeroLife`

  - `sample()` not affect running of `tic()`
  - make sure you working in `main()` when writing code about `String`, `System` and so on in `java.lang`
  - I add `beatles-hey_jude.mid` and `beatles-yesterday.mid` FROM [bitmidi](https://bitmidi.com/)
    - but not great when listening, we need better Player

### proj1ec

- you can use a message of `String` to contain information

### proj2 and proj2ec

start time: *2022.09.18*
end time: *2022.10.01*

- [gitlet-design](https://github.com/duilec/CS61B-spring2021/blob/main/proj2/gitlet-design.md)

### proj3

start time:  *2022.10.20*
end time:  *2022.11.07*

- phase1

  - Generating world: I use **A*** of graph of algorithm to find shortest path in order to connect room and floor
    but I used try directly connecting, it will cause many "across" and will fill floors in all world in the end. 
    I wasted lots of time and used A* in the second time.

- phase2(done with primary but not secondary)
  Learning interactivity in java

  - display information
  - typing
  - mouse clicking

  I choose implement "**turn on/off right in room**"

  I save process(file) by some Utils from **proj2(`Utils`)**.
  
- conclusion
  I have some experience to simply my code

  - OOP
  - reducing repeating code
  - set some *mark* to compare
  - level abstraction

- other

  -  **I DON'T WRITE CODE ABOUT CHECKING OF ERROR**(particularly in typing) 
    i.e. you must type right char in keyboard
  - I don't use any method in `RandomUtils`

## homework

### hw0

time: *2022.08.09*

- I only write a version of "for" in `GetMax`

