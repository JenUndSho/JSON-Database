import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String stringLine = reader.readLine();
        reader.close();
        String s2 = stringLine.replaceAll("( )+", " ");
        if (s2.isEmpty() || s2.equals(" "))
            System.out.println(0);
        else
            System.out.println(s2.trim().split(" ").length);
    }
}