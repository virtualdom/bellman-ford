import algorithms.BellmanFord;
import java.util.Scanner;
import java.io.*;

public class Driver {
  static int n = 0;
  public static void main(String[] args) throws IOException {
    String INPUT_FILE = "./connectivity.txt";

    // Read input files
    int leader = -1;
    int[][] connectivity = null;
    Scanner inputScanner = null;
    String result = null;
    try {
      inputScanner = new Scanner(new File(INPUT_FILE));
    } catch (FileNotFoundException e) {
      System.err.println(INPUT_FILE + " not found!");
      return;
    }

    String[] val = null;

    result = inputScanner.nextLine();
    if(result.contains(",")) {
      val = result.split(",");
    } else {
      System.err.println(INPUT_FILE + " should begin with the number of processes, followed by whitespace, followed by the leader process ID.");
      return;
    }

    if(result.matches(".*\\d.*")) {
      n = Integer.valueOf(val[0]);
      leader = Integer.valueOf(val[1]) - 1;
    } else {
      System.err.println(INPUT_FILE + " should begin with the number of processes.");
      return;
    }

    connectivity = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        connectivity[i][j] = -2;

    for (int row = 0; row < n; row++) {
      for(int col = 0; col < n; col++) {
        if (inputScanner.hasNextInt())
          connectivity[row][col] = inputScanner.nextInt();
        if(connectivity[row][col] < -1) {
          System.err.println(INPUT_FILE + " should only contain values that represent nonnegative edge weights or -1 to represent no edge");
          System.err.println("There must be " + (n * n) + " integers in the connectivity matrix (" + n + " x " + n + ")");
          return;
        }
      }
    }

    // Run Bellman-Ford and print tree
    BellmanFord bf = new BellmanFord(n, leader, connectivity);
    bf.start();
    bf.printTree();
  }
}
