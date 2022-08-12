package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();
        int M = 10000;
        for (int i = 1000; i <= 128000; i *= 2 ){
            Ns.addLast(i);
            SLList<Integer> testSLList = new SLList<Integer>();
            for (int j = 0; j < i; j++){
                testSLList.addLast(i);
            }
            double startTime = System.currentTimeMillis();
            int opCount = 0;
            for (int j = 0; j < M; j++){
                testSLList.getLast();
                opCount += 1;
            }
            double endTime = System.currentTimeMillis();
            double time = (endTime - startTime) / 1000;
            opCounts.addLast(opCount);
            times.addLast(time);
        }
        // we must travel all node in SLList to get the last node,
        // so getlast() is slow in the big SLList
        printTimingTable(Ns, times, opCounts);
    }

}
