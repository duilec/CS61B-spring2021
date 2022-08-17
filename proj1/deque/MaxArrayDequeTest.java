package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;

// test: date and using method(like function of main())
// hint: You’ll likely be creating multiple Comparator<T> classes to test your code
// so, we need build different comparator to test in MaxArrayDeque

public class MaxArrayDequeTest {
    @Test
    public void maxWithIntComparatorTest() {
        // construct an instance of class by using "new ClassName()"
        // when the class not pass argument to constructor
        IntComparator intComparator = new IntComparator();
        MaxArrayDeque<Integer> maxArrayDeque= new MaxArrayDeque<>(intComparator);
        for (int i = 0; i <= 100000; i++){
            maxArrayDeque.addFirst(i);
        }
        int actual = maxArrayDeque.max();
        assertEquals(100000, actual);

        int actual1 = maxArrayDeque.max(intComparator);
        assertEquals(100000, actual1);
    }

    private static class IntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer i1, Integer i2){
            return i1 - i2;
        }
    }

    @Test
    public void maxWithDoubleComparatorTest() {
        DoubleComparator doubleComparator = new DoubleComparator();
        MaxArrayDeque<Double> maxArrayDeque= new MaxArrayDeque<>(doubleComparator);
        for (double i = 0; i <= 100000; i++){
            maxArrayDeque.addFirst(i);
        }
        double actual = maxArrayDeque.max();
        // you should pass delta, when using assertEquals() to assert type of double
        assertEquals(100000, actual,0.0);

        double actual1 = maxArrayDeque.max(doubleComparator);
        assertEquals(100000, actual1,0.0);
    }

    private static class DoubleComparator implements Comparator<Double>{
        @Override
        // we should have same name and signature when overriding
        // so, we can't use "public double compare(Double d1, Double d2)"
        public int compare(Double d1, Double d2){
            return (int)(d1 - d2);
        }
    }

    @Test
    public void maxWithStringLengthComparatorTest() {
        StringLengthComparator stringLengthComparator = new StringLengthComparator();
        MaxArrayDeque<String> maxArrayDeque= new MaxArrayDeque<>(stringLengthComparator);

        maxArrayDeque.addFirst("HelloWorld");
        maxArrayDeque.addFirst("Hello World!");

        assertEquals("Hello World!", maxArrayDeque.max());
        assertEquals("Hello World!", maxArrayDeque.max(stringLengthComparator));
    }

    private static class StringLengthComparator implements Comparator<String>{
        @Override
        public int compare(String d1, String d2){
            return d1.length() - d2.length();
        }
    }
}
