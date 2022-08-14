package deque;

/** Array Deque.
 *  @author Huang Jinhong
 */

//         0 1  2 3 4 5 6 7
// items: [6 9 -1 2 0 0 0 0 ...]
// size: 5

/* Invariants:
 addLast: The next item we want to add, will go into position size
 getLast: The item we want to return is in position size - 1
 size: The number of items in the deque should be size.
*/

//public void addFirst(T item)
//public void addLast(T item)
//public boolean isEmpty()
//public int size()
//public void printDeque()
//public T removeFirst() 25%
//public T removeLast() 25%
//public T get(int index)

//b03) AD-basic: fill up, empty, fill up again. (0.0/1.333)
//    Test Failed!
//            Failed on 1th removeFirst() operation in ArrayDeque expected:<6> but was:<7>
//        at AGTestArrayDeque.fillUpEmptyFillUpAgain:96 (AGTestArrayDeque.java)
//        at AGTestArrayDeque.fillUpEmptyFillUpTest:110 (AGTestArrayDeque.java)
//b04) AD-basic: multiple ADs. (0.0/1.333)
//    Test Failed!
//            Failed on 1th removeFirst() operation in ArrayDeque 1 expected:<6> but was:<7>
//        at AGTestArrayDeque.twoArrayDequesTest:138 (AGTestArrayDeque.java)
//        at AGTestArrayDeque.multiADTest:149 (AGTestArrayDeque.java)
//b06) AD-basic: negative size. (0.0/1.333)
//    Test Failed!
//            java.lang.ArrayIndexOutOfBoundsException: Index -2 out of bounds for length 8
//            at deque.ArrayDeque.removeLast:83 (ArrayDeque.java)
//            at AGTestArrayDeque.negSizeTest:177 (AGTestArrayDeque.java)

//  We strongly recommend that you treat your array as circular for this exercise.
//  In other words, if your front item is at position zero, and you addFirst,
//  the new front should loop back around to the end of the array
//  (so the new front item in the deque will be the last item in the underlying array).

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** Creates an empty Array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 8 / 2;
        nextLast = (8 / 2) + 1;
    }

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity, boolean isAdd) {
        T[] newItems = (T[]) new Object[capacity];
        // if resize() called by addFirst/Last, keeping copy to all old items in middle part(from "size/2" to "size/2 + size - 1")
        // otherwise, resize() called by removeFirst/Last, keeping the copy to all old items from index of 0
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
    public void addLast(T item) {
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
    public T get(int i) {
         int index = nextFirst + i + 1;
         if (index > 0){
             return items[index % items.length];
         }
        return items[index + items.length];
    }

    /** Returns the number of items in the deque. */
    public int size() {
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
}

