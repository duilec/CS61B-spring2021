package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;

public class ArrayDequeTest {
    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        assertTrue("A newly initialized adeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  ad1 = new ArrayDeque<String>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigadequeTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 100000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 50000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 99999; i > 50000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigadequeTest2() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 100000; i++) {
            ad1.addFirst(i);
        }

        for (double i = 0; i < 50000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }

        for (double i = 99999; i > 50000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }
    }


    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void addGetTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 100000; i++) {
            ad1.addLast(i);
        }
        for (int i = 0; i < 100000; i++) {
            assertEquals("Should have the same value", i, ad1.get(i), 0.0);
        }
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    // note: we add First from 0 to 3, then, the list is "3->2->1->0", the "3" is first
    public void addGetTest2() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 100000; i++) {
            ad1.addFirst(i);
        }
        int index = 0;
        for (int i = 99999; i >= 0; i--) {
            assertEquals("Should have the same value", i, ad1.get(index), 0.0);
            index += 1;
        }
    }

    @Test
    /* test Iterator */
    public void addThenIteratorTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            ad1.addLast(i);
        }

        Iterator<Integer> ader = ad1.iterator();

        int expected = 0;
        while (ader.hasNext()) {
            assertEquals("Should have the same value", expected, ader.next(), 0.0);
            expected += 1;
        }
    }

    @Test
    /* test enhanced for with Iterator */
    // You should not have your Deque interface implement Iterable<T>
    // But you should have your ArrayDeque implement Iterable<T>
    public void ForIteratorTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            ad1.addLast(i);
        }

        int expected = 0;
        for (int i : ad1) {
            assertEquals("Should have the same value", expected, i, 0.0);
            expected += 1;
        }
    }

    @Test
    public void addThenEqualTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            ad.addLast(i);
        }

        LinkedListDeque<Integer> lld = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            lld.addLast(i);
        }

        assertEquals(true, ad.equals(lld));
    }
}
