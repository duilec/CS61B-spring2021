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

public class ArrayDeque<T> {
    private T[] items;
    private int size;

    /** Creates an empty Array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }

    /** Resizes the underlying array to the target capacity.
     * changed = 0, copying: underlying array from items[0], target array from newItems[0]
     * changed = 1, copying: underlying array from items[0], target array from newItems[1]
     * changed = 2, copying: underlying array from items[1], target array from newItems[0]
     * */
    private void resize(int capacity, int changed) {
        T[] newItems = (T[]) new Object[capacity];
        if (changed == 0){ // addLast() and removeLast()
            System.arraycopy(items, 0, newItems, 0, size);
        }else if (changed == 1){ // addFirst
            System.arraycopy(items, 0, newItems, 1, size);
        }else if (changed == 2){ // removeFirst
            System.arraycopy(items, 1, newItems, 0, size);
        }
        items = newItems;
    }

    /** Adds item to the front of the deque. */
    public void addFirst(T item){
        if (size == items.length) {
            resize(size * 2, 1);
        } else {
            for (int i = size; i > 0; i += 1){
                items[i] = items[i - 1];
            }
        }
        items[0] = item;
        size = size + 1;
    }

    /** Inserts X into the back of the deque. */
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2, 0);
        }
        items[size] = item;
        size = size + 1;
    }

    /** Removes the front of the deque. */
    public T removeFirst(){
        if (isEmpty()){
            return null;
        }
        T item = items[0];
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4, 2);
        } else {
            for (int i = 0; i < size - 1; i += 1){
                items[i] =  items[i + 1];
            }
            items[size - 1] = null;
        }
        size = size - 1;
        return item;
    }

    /** Removes the back of the deque. */
    public T removeLast(){
        if (isEmpty()){
            return null;
        }
        T item = items[size - 1];
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4, 0);
        }
        items[size - 1] = null;
        size = size - 1;
        return item;
    }

    /** Gets the ith item in the deque (0 is the front). */
    public T get(int i) {
        return items[i];
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
        // TODO
        for (int i = 0; i < size; i += 1){
            if (i == size - 1){
                System.out.println(items[i]);
                return;
            }
            System.out.print(items[i]+" -> ");
        }

    }
}

