package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */

// we don't need main() in test of Junit
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> buggyAList = new BuggyAList<Integer>();

        // three add
        for (int i = 4; i < 7; i++){
            buggyAList.addLast(i);
        }
        // three remove one time
        for (int expected  = 6 ; expected > 3 ; expected--){
            int actual = buggyAList.removeLast();
            assertEquals(expected, actual);
        }
    }
    // without comparison
    // not clean up log
//    @Test
//    public void randomizedTest(){
//        AListNoResizing<Integer> L = new AListNoResizing<Integer>();
//
//        int N = 500;
//        for (int i = 0; i < N; i += 1) {
//            int operationNumber = StdRandom.uniform(0, 4);
//            if (operationNumber == 0) {
//                // addLast
//                int randVal = StdRandom.uniform(0, 100);
//                L.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
//            } else if (operationNumber == 1) {
//                // size
//                int size = L.size();
//                System.out.println("size: " + size);
//            }else if (operationNumber == 2){
//                // getLast
//                if (L.size() == 0){
//                    continue;
//                }
//                int val = L.getLast();
//                System.out.println("getLast(" + val + ")");
//            } else if (operationNumber == 3) {
//                // removeLast
//                if (L.size() == 0){
//                    continue;
//                }
//                int val = L.removeLast();
//                System.out.println("removeLast(" + val + ")");
//            }
//        }
//    }

    // without comparison
    // clean up log
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> LA = new AListNoResizing<Integer>();
        BuggyAList<Integer> LB = new BuggyAList<Integer>();
        int N = 5000;

        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                // AListNoResizing
                LA.addLast(randVal);
                // BuggyAList
                LB.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                // AListNoResizing
                LA.size();
                // BuggyAList
                LB.size();
            }else if (operationNumber == 2){
                // getLast
                if (LA.size() != 0){
                    LA.getLast();
                }
                if (LB.size() != 0){
                    LB.getLast();
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (LA.size() != 0){
                    LA.removeLast();
                }
                if (LB.size() != 0){
                    LB.removeLast();
                }
            }
        }
    }

    // this test from class
//    @Test
//    public void testThreeAddThreeRemove() {
//        AListNoResizing<Integer> correct = new AListNoResizing<Integer>();
//        BuggyAList<Integer> broken = new BuggyAList<Integer>();
//
//        correct.addLast(5);
//        correct.addLast(10);
//        correct.addLast(15);
//
//        broken.addLast(5);
//        broken.addLast(10);
//        broken.addLast(15);
//
//        assertEquals(correct.size(), broken.size());
//
//        assertEquals(correct.removeLast(), broken.removeLast());
//        assertEquals(correct.removeLast(), broken.removeLast());
//        assertEquals(correct.removeLast(), broken.removeLast());
//    }
}
