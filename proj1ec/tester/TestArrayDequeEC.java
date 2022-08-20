package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void add10000ThenRemoveFirst10000Test(){
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads1 = new ArrayDequeSolution<>();

        String message = "";
        for (int i = 0; i < 100; i += 1) {
            Integer number = StdRandom.uniform(1, 100);
            if (number < 50){
                sad1.addFirst(number);
                ads1.addFirst(number);
                message += "addFirst(" + number + ")\n";
            } else {
                sad1.addLast(number);
                ads1.addLast(number);
                message += "addLast(" + number + ")\n";
            }
        }

        Integer actual;
        Integer expected;
        for (int i = 0; i < 100; i += 1) {
            Integer numberZeroOrOne = StdRandom.uniform(0, 2);
            if (numberZeroOrOne == 1) {
                actual = sad1.removeFirst();
                expected = ads1.removeFirst();
                message += "removeFirst()\n";
            } else {
                actual = sad1.removeLast();
                expected = ads1.removeLast();
                message += "removeLast()\n";
            }
            assertEquals(message, expected, actual);

        }
    }
}
