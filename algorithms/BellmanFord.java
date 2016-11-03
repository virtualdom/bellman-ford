package algorithms;

import algorithms.AsynchLink;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class BellmanFord {
  private static final int NO_ANSWER = 0;
  private static final int ACK = 1;
  private static final int NACK = 2;
  private static final int PARENT = 3;

  public class Process extends Thread {
    int id = -1;
    int parent = -1;
    int distance = Integer.MAX_VALUE;
    boolean leader;
    int [] responses = new int[n];

    public Process (int id, boolean leader) {
      this.id = id;
      this.leader = leader;
      if (leader) {
        distance = 0;
        parent = -2;
      }
      resetResponses();
    }

    private void resetResponses () {
      for (int j = 0; j < n; j++)
        if (links[id][j] == null) responses[j] = NACK;
        else responses[j] = NO_ANSWER;
    }

    private void writeToMaster (String message) {
      try { toMaster.get(id).put(message); }
      catch (InterruptedException e) {
        System.err.println("Write from process " + id + " to master failed.");
        System.exit(0);
      }
    }

    private String readFromMaster () {
      try { return toProcess.get(id).take(); }
      catch (InterruptedException e) {
        System.err.println("Read by process " + id + " from process master failed.");
        System.exit(0);
        return "";
      }
    }

    private int parse (String message) {
      try { return Integer.parseInt(message.substring(2)); }
      catch (NumberFormatException e) {
        System.out.println("Failed to parse: \"" + message + "\"");
        System.exit(0);
        return -9999;
      }
    }

    @Override
    public void run () {
      if (leader) {
        for (int i = 0; i < n; i++)
          if (links[id][i] != null) links[id][i].write("E " + distance);
      }

      writeToMaster("-");

      do {
        readFromMaster();

        // Process only explore messages
        // Explore message processing goes here

        // Process only NACKS/ACKS
        // (N)ACK processing goes here

        if (links[parent][id].getMessageType() == 'D') {
          for (int i = 0; i < n; i++) {
            if (responses[i] == ACK) {
              links[id][i].write("D");
            }
          }
          writeToMaster(Integer.toString(parent));
          return;
        }

        writeToMaster("-");
      } while (true);

    }
  }

  int n;
  int leader;
  int [] parents;
  AsynchLink [][] links;
  ArrayList<ArrayBlockingQueue<String>> toMaster;
  ArrayList<ArrayBlockingQueue<String>> toProcess;

  public BellmanFord (int n, int leader, int [][] connectivity) {
    this.n = n;
    this.leader = leader;

    parents = new int[n];
    links = new AsynchLink[n][n];
    toMaster = new ArrayList<ArrayBlockingQueue<String>>(n);
    toProcess = new ArrayList<ArrayBlockingQueue<String>>(n);

    for (int i = 0; i < n; i++) {
      parents[i] = -1;
      toMaster.add(new ArrayBlockingQueue<String>(1));
      toProcess.add(new ArrayBlockingQueue<String>(1));

      for (int j = 0; j < n; j++)
        if (connectivity[i][j] != -1)
          links[i][j] = new AsynchLink(connectivity[i][j]);
    }
  }

  private void writeToProcess (int i, String message) {
    try { toProcess.get(i).put(message); }
    catch (InterruptedException e) {
      System.err.println("Master could not write to process " + i);
      System.exit(0);
    }
  }

  private String readFromProcess (int i) {
    try { return toMaster.get(i).take(); }
    catch (InterruptedException e) {
      System.err.println("Master could not read from process " + i);
      System.exit(0);
      return "";
    }
  }

  private boolean parentsFull () {
    for (int i = 0; i < n; i++)
      if (parents[i] == -1) return false;
    return true;
  }

  private void updateLinkTimes () {
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        if (links[i][j] != null)
          links[i][j].incrementTime();
  }

  public void printTree () {

    // ************************************
    // print final tree using parents array
    // ************************************

  }

  public void start () {
    String message;
    Thread [] processes = new Thread [n];

    for (int i = 0; i < n; i++) {
      processes[i] = new Process(i, i == leader);
      processes[i].start();
    }

    while (!parentsFull()) {
      for (int i = 0; i < n; i++) {
        if (parents[i] == -1) {
          message = readFromProcess(i);
          if (!message.equals("-")) parents[i] = Integer.parseInt(message);
        }
      }

      updateLinkTimes();

      for (int i = 0; i < n; i++)
        if (parents[i] == -1) writeToProcess(i, "-");
    }
  }
}
