package deque;

import java.util.Iterator;

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

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{
    private class Node {
        public T item;
        public Node prev;
        public Node next;

        public Node(T i, Node p, Node n){
            item = i;
            prev = p;
            next = n;
        }
    }

    // The first item (if it exists) is at sentinel.next.
    private Node sentinel;
    private int size;

    /** Creates an empty LinkedListDeque. */
    public LinkedListDeque(){
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
    @Override
    public void addFirst(T item){
        Node rest = sentinel.next;
        sentinel.next = new Node(item, sentinel, rest);
        rest.prev = sentinel.next;
        size += 1;
    }

    /** Adds item to the back of the deque. */
    @Override
    public void addLast(T item){
        Node back = sentinel.prev;
        sentinel.prev = new Node(item, back, sentinel);
        back.next = sentinel.prev;
        size += 1;
    }

    /** Removes the front of the deque. */
    @Override
    public T removeFirst(){
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
    @Override
    public T removeLast(){
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
    @Override
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
    @Override
    public int size(){
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    @Override
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

    public Iterator<T> iterator(){
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int cnt;

        public LinkedListDequeIterator() {
            cnt = 0;
        }

        public boolean hasNext() {
            return cnt < size();
        }

        public T next() {
            T returnItem = get(cnt);
            cnt += 1;
            return returnItem;
        }
    }

    //Returns whether or nor the parameter o is equal to the Deque.
    // o is considered equal if it is a Deque and if it contains the same contents
    // (as goverened by the generic T’s equals method) in the same order.
    // note: use "equals" instead of "==", when comparing of content from object
    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)){
            return false;
        }

        Deque<T> obj = (Deque<T>)o;
        if (obj.size() != this.size()){
            return false;
        }
        for(int i = 0; i < obj.size(); i += 1){
            T itemFromObj =  obj.get(i);
            T itemFromThis = this.get(i);
            if (!itemFromObj.equals(itemFromThis)){
                return false;
            }
        }
        return true;
    }
}
