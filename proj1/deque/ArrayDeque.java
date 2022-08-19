package deque;

import java.util.Iterator;

/** Array Deque.
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

public class ArrayDeque<T> implements Deque<T> ,Iterable<T>{
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** Creates an empty Array deque. */
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 8 / 2;
        nextLast = (8 / 2) + 1;
    }

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity, boolean isAdd){
        T[] newItems = (T[]) new Object[capacity];
        // if resize() called by addFirst/Last, copy to all old items in middle part
        // (i.e. from "size/2" to "size/2 + size - 1")
        // otherwise, resize() called by removeFirst/Last, copy to all old items from index of first
        // note: "size" is old "size"
        if (isAdd){
            System.arraycopy(items, nextLast, newItems, size / 2, size - nextLast);
            System.arraycopy(items, 0, newItems, (size - nextLast) + size / 2, nextLast);
        } else {
            System.arraycopy(items, (nextFirst + 1) % items.length, newItems, 0, size);
        }
        items = newItems;
    }

    /** Adds item to the front of the deque. */
    @Override
    public void addFirst(T item){
        if (size == items.length) {
            resize(size * 2, true);
            // keep circular deque,
            // the index of first after nextFirst and the index of last before nextLast
            nextFirst = (size / 2) - 1;
            nextLast = size / 2 + size;
        }
        // "-1 % items.length == -1" in java
        items[nextFirst] = item;
        if ((nextFirst - 1) % items.length == -1){
            nextFirst = (nextFirst - 1) + items.length;
        } else {
            nextFirst = (nextFirst - 1) % items.length;
        }
        size = size + 1;
    }

    /** Inserts X into the back of the deque. */
    @Override
    public void addLast(T item){
        if (size == items.length) {
            resize(size * 2, true);
            // keep circular deque,
            // the index of first after nextFirst and the index of last before nextLast
            nextFirst = (size / 2) - 1;
            nextLast = size / 2 + size;
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size = size + 1;
    }

    /** Removes the front of the deque. */
    @Override
    public T removeFirst(){
        if (isEmpty()){
            return null;
        }
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4, false);
            // After reducing to 25%, nextFirst and nextLast resets to new "size"
            nextFirst = size;
            nextLast = size;
        }
        nextFirst = (nextFirst + 1) % items.length;
        T item = items[nextFirst];
        items[nextFirst] = null;
        size = size - 1;
        return item;
    }

    /** Removes the back of the deque. */
    @Override
    public T removeLast(){
        if (isEmpty()){
            return null;
        }
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4,false);
            // After reducing to 25%, nextFirst and nextLast resets to new "size"
            nextFirst = size;
            nextLast = size;
        }
        // "-1 % items.length == -1" in java
        if ((nextLast - 1) % items.length == -1){
            nextLast = (nextLast - 1) + items.length;
        } else {
            nextLast = (nextLast - 1) % items.length;
        }
        T item = items[nextLast];
        items[nextLast] = null;
        size = size - 1;
        return item;
    }

    /** Gets the ith item in the deque (0 is the front). */
    // note: when add first from 0 to 3, then, the list is "3->2->1->0", the "3" is first
    @Override
    public T get(int i){
         int index = nextFirst + i + 1;
         if (index > 0){
             return items[index % items.length];
         }
        return items[index + items.length];
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
        int tempSize = size();
        int cnt = nextFirst + 1;
        while(tempSize > 0){
            if (tempSize == 1){
                System.out.println(items[cnt]);
                return;
            }
            System.out.print(items[cnt]+" -> ");
            cnt = (cnt + 1) % items.length;
            tempSize -= 1;
        }
    }

    // The Deque objects we’ll make are iterable (i.e. Iterable<T>)
    // so we must provide this method to return an iterator.
    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int cnt;

        public ArrayDequeIterator() {
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

