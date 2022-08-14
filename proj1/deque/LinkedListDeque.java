package deque;

/** Linked List Deque.
 *  @author Huang Jinhong
 */


//public void addFirst(T item)
//public void addLast(T item)
//public boolean isEmpty()
//public int size()
//public void printDeque()
//public T removeFirst()
//public T removeLast()
//public T get(int index)

//Testing deque.LinkedListDeque.java
//            The following interfaces are missing:
//            *  Iterable<T>
//
//The following constructors should be removed:
//        *  public deque.LinkedListDeque(T)
//
//        The following methods are missing:
//        *  public T getRecursive(int)
//        *  public boolean equals(Object)
//        *  public java.util.Iterator<T> iterator()

public class LinkedListDeque<T> {
    private class Node {
        public T item;
        public Node prev;
        public Node next;

        public Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    // The first item (if it exists) is at sentinel.next.
    private Node sentinel;
    private int size;

    /** Creates an empty LinkedListDeque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

//    /** Creates a LinkedListDeque with an element of item. */
//    public LinkedListDeque(T item) {
//        sentinel = new Node(null, null, null);
//        sentinel.next = new Node(item, sentinel, sentinel);
//        sentinel.prev = sentinel.next;
//        size = 1;
//    }

    /** Adds item to the front of the deque. */
    public void addFirst(T item) {
        Node rest = sentinel.next;
        sentinel.next = new Node(item, sentinel, rest);
        rest.prev = sentinel.next;
        size += 1;
    }

    /** Adds item to the back of the deque. */
    public void addLast(T item) {
        Node back = sentinel.prev;
        sentinel.prev = new Node(item, back, sentinel);
        back.next = sentinel.prev;
        size += 1;
    }

    /** Removes the front of the deque. */
    public T removeFirst() {
        if (isEmpty()){
            return null;
        }
        Node oldFront = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        oldFront.prev = null;
        oldFront.next = null;
        size -= 1;
        return oldFront.item;
    }

    /** Removes the back of the deque. */
    public T removeLast() {
        if (isEmpty()){
            return null;
        }
        Node oldBack = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        oldBack.prev = null;
        oldBack.next = null;
        size -= 1;
        return oldBack.item;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. */
    public T get(int i){
        if (isEmpty() || i < 0){
            return null;
        }
        Node p = sentinel.next;
        int count = 0;
        /* Advance p to the end of the deque. */
        while (p != sentinel) {
            if (i == count){
                return p.item;
            }
            count += 1;
            p = p.next;
        }
        return null;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * but uses recursion*/
    public T getRecursive(int i){
        if (isEmpty() || i < 0){
            return null;
        }
        return getRecursiveHelper(i, sentinel.next);
    }

    private T getRecursiveHelper(int i, Node p){
        if (p == sentinel){
            return null;
        }
        if (i == 0){
            return p.item;
        }
        return getRecursiveHelper(i - 1, p.next);
    }

    /** Returns the number of items in the deque. */
    public int size(){
        return size;
    }

    /** Returns true if deque is empty, false otherwise.*/
    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    public void printDeque(){
        Node p = sentinel.next;

        if (p == sentinel){
            System.out.println("This is empty deque");
            return;
        }

        while (p != sentinel) {
            if (p.next == sentinel){
                System.out.print(p.item);
                System.out.println();
                return;
            }
            System.out.print(p.item + " -> ");
            p = p.next;
        }
    }
}
