package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> testAList = new AList<Integer>();
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();
        for (int i = 1000; i <= 128000; i *= 2 ){
            Ns.addLast(i);
            double startTime = System.currentTimeMillis();
            int opCount = 0;
            for (int j = 0; j < i; j++){
                testAList.addLast(j);
                opCount += 1;
            }
            double endTime = System.currentTimeMillis();
            double time = (endTime - startTime) / 1000;
            opCounts.addLast(opCount);
            times.addLast(time);
        }
        // the time per addLast operation is the same for N = 1000 and N = 2000.
        // due to issues like caching, process switching, branch prediction,
        // etc. which you’ll learn about if you take 61C
        printTimingTable(Ns, times, opCounts);
    }
}
