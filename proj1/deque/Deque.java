package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T>{
    public void addFirst(T item);
    public void addLast(T item);
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);

    /** Returns true if deque is empty, false otherwise.*/
    default public boolean isEmpty(){
        if (size() == 0){
            return true;
        }
        return false;
    }
}
