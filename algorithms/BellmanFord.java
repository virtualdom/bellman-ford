package algorithms;

import algorithms.AsynchLink;
import java.util.ArrayList;
import java.util.LinkedList;
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

      int tempParent, tempDistance, message;  //Might want to put these in constructor
      String tmpString;
      char messageType;

      do {
        readFromMaster();

        // Process only explore messages
        // Explore message processing goes here

        tempParent = Integer.MIN_VALUE;
        tempDistance = distance;

        for(int i = 0; i < n; i++)
        {
          if(links[i][id] != null && links[i][id].getMessageType() == 'E')
          {
            tmpString = links[i][id].read();
            message = parse(tmpString);

            if(message + links[i][id].getWeight() < tempDistance)
            {
              if (parent != -1 || tempParent != Integer.MIN_VALUE) {
                if(tempParent == Integer.MIN_VALUE)
                  links[id][parent].write("N " + (distance - links[parent][id].getWeight()));
                else
                  links[id][tempParent].write("N " + (tempDistance - links[tempParent][id].getWeight()));
              }

              tempParent = i;
              tempDistance = message + links[i][id].getWeight();
              resetResponses();
            }
            else
            {
              links[id][i].write("N " + message);
              if (responses[i] == ACK)
                responses[i] = NACK;
            }
          }
        }

        if (tempParent != Integer.MIN_VALUE)
        {
          parent = tempParent;
          distance = tempDistance;

          for(int i = 0; i < n; i++)
            if(links[id][i] != null && parent != i)
              links[id][i].write("E " + distance);
        }

        // Process only NACKS/ACKS
        // (N)ACK processing goes here
        for (int i = 0; i < n; i++) {
          if (links[i][id] != null) {
            messageType = links[i][id].getMessageType();
            if (messageType == 'A' || messageType == 'N') {
              message = parse(links[i][id].read());

              if (message == distance) {
                responses[i] = messageType == 'A' ? ACK : NACK;
              }
            }
          }
        }

        boolean responseFull = true;

        for (int i = 0; i < n; i++) {
          if (parent != i && responses[i] != NACK && responses[i] != ACK) {
            responseFull = false;
            break;
          }
        }

        if (responseFull) {
          if (leader) {
            for (int i = 0; i < n; i++) {
              if (links[id][i] != null && responses[i] == ACK)
                links[id][i].write("D");
            }

            writeToMaster(Integer.toString(parent));
            return;

          } else if (responses[parent] == NO_ANSWER) {
            links[id][parent].write("A " + (distance - links[id][parent].getWeight()));
            responses[parent] = PARENT;
          }
        }

        // DONE MESSAGES BEGIN HERE
        if (!leader && parent != -1 && links[parent][id].getMessageType() == 'D') {
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

  private void printChild (int parent, int level) {
    for (int i = 0; i < n; i++) {
      if (parents[i] == parent) {
        for (int j = 0; j < level; j++) {
          System.out.print("| ");
        }
        System.out.print("" + (i + 1) + "\n");
        printChild(i, level + 1);
      }
    }
  }

  public void printTree () {
    LinkedList<Integer> stack = new LinkedList<Integer>();

    System.out.println(leader + 1);
    printChild(leader, 1);

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
