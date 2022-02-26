import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task {
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int can = reader.read();
    StringBuilder s = new StringBuilder();
    while (can != -1) {
      char character = (char) can;
      s.append(character);
      can = reader.read();
    }
    reader.close();
    System.out.println(s.reverse());
  }
}
