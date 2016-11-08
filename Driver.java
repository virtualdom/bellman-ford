import algorithms.BellmanFord;
import java.util.Scanner;
import java.io.*;

public class Driver {
  public static void main(String[] args) {
    String INPUT_FILE = "./connectivity.txt";

    // Read input files
    int n = 0;
    int leader = -1;
    int[][] connectivity;
    Scanner inputScanner;

    try {
      inputScanner = new Scanner(new File(INPUT_FILE));
    } catch (FileNotFoundException e) {
      System.err.println(INPUT_FILE + " not found!");
      return;
    }

    if (inputScanner.hasNextInt()) n = inputScanner.nextInt();
    else {
      System.err.println(INPUT_FILE + " should begin with the number of processes.");
      return;
    }

    if (inputScanner.hasNextInt()) leader = inputScanner.nextInt() - 1;
    else {
      System.err.println(INPUT_FILE + " should begin with the number of processes, followed by whitespace, followed by the leader process ID.");
      return;
    }

    connectivity = new int [n][n];

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (inputScanner.hasNextInt()) connectivity[i][j] = inputScanner.nextInt();
        else {
          System.err.println(INPUT_FILE + " should contain " + (n * n) + " integers (a " + n + " x " + n + " matrix of integer values)");
          return;
        }

        if (connectivity[i][j] < -1) {
          System.err.println(INPUT_FILE + " should only contain values that represent nonnegative edge weights or -1 to represent no edge");
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
