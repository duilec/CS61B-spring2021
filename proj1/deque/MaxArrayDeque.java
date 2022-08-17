package deque;
import java.util.Comparator;

/** Max Array Deque.
 *  @author Huang Jinhong
 */

// define: additional method and constructor
// public MaxArrayDeque(Comparator<T> c)
// public T max(Comparator<T> c)
// public T max()

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    // store the pass argument of "c" to "comparator"
    // when using max(), we can use "comparator"
    private Comparator<T> comparator;

    /** creates a MaxArrayDeque with the given Comparator. */
    // store the pass argument of "c" to "comparator"
    // when using max(), we can use "comparator"
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    /** returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null.*/
    // note: we use method of comparator to compare
    public T max(Comparator<T> c) {
        if(isEmpty()){
            return null;
        }
        int maxIndex = 0;
        for(int i = 1; i < size(); i++){
            if(c.compare(get(maxIndex), get(i)) < 0){
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

    /** returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null.*/
    public T max() {
        return max(comparator);
    }
}

