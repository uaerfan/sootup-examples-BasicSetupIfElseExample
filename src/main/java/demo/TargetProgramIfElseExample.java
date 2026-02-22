package demo;
public class TargetProgramIfElseExample {

    public static int maximum(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public static void main(String[] args) {
        System.out.println(maximum(3, 4));
    }
    
}
