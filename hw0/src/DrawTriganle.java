public class DrawTriganle {
    public static void main(String[] args) {
        drawTriangle(10);
    }
    public static void drawTriangle(int N) {
        int row = 0;  // form 0 to 4
        int column = 1; // form 1 to 5
        while (row < N) {
            int temp_column = column;
            while (temp_column > 0) {
                if (temp_column == 1 && row == N - 1) {
                    System.out.print("*");
                } else if (temp_column == 1) {
                    System.out.println("*");
                } else {
                    System.out.print("*");
                }
                temp_column -= 1;
            }
            column += 1;
            row += 1;
        }
    }
}
