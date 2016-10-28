package algorithms;

import algorithms.Message;
import java.util.LinkedList;
import java.util.Random;

/*

  AsynchLink
  -------
  This class represents a link between two nodes.
  It keeps track of the current time/pulse number
  to know whether or not a particular message has
  arrived to the destination node. It also keeps
  track of the `latestArrival` value (the arrival
  time of the last message added to the FIFO queue)
  so that if another message is added to the queue
  and is scheduled to arrive before its preceding
  message, the AsynchLink postpones the second
  message's arrival until after the latestArrival.

*/

public class AsynchLink {
  int timeUnit;
  int weight;
  int latestArrival;
  Random delayGenerator;
  LinkedList<Message> messageQueue;

  public AsynchLink (int weight) {
    this.weight = weight;
    timeUnit = 0;
    latestArrival = 0;
    delayGenerator = new Random();
    messageQueue = new LinkedList<Message>();
  }

  public void incrementTime () { timeUnit++; }
  public int getWeight () { return weight; }

  public String read () {
    if (messageQueue.isEmpty() || messageQueue.peek().getTime() > timeUnit)
      return "-";
    else return messageQueue.remove().getMessage();
  }

  public void write (String message) {
    int arrivalTime = timeUnit + delayGenerator.nextInt(15) + 1;

    if (messageQueue.isEmpty() || arrivalTime > latestArrival) {
      messageQueue.add(new Message(message, arrivalTime));
      latestArrival = arrivalTime;
    }
    else messageQueue.add(new Message(message, ++latestArrival));
  }

  public char getMessageType () {
    if (messageQueue.isEmpty() || messageQueue.peek().getTime() > timeUnit)
      return '-';
    else return messageQueue.peek().getMessage().charAt(1);
  }
}
