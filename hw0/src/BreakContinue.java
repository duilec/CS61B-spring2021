public class BreakContinue {
    public static void windowPosSum(int[] a, int n) {
        /** your code here */
        for (int i = 0; i < a.length; i++){
            if (a[i] < 0){
                continue;
            }
            int sum = 0;
            // note: use "j" as index and count below loop
            for (int j = i; j < i + n; j++){
                if (j == a.length){
                    break;
                }
                sum += a[j];
            }
            a[i] = sum;
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, -3, 4, 5, 4};
        int n = 3;
        windowPosSum(a, n);

        // Should print 4, 8, -3, 13, 9, 4
        System.out.println(java.util.Arrays.toString(a));
    }
}
