package algorithms;

/*

  Message
  -------
  The purpose of this class is simply
  to be able to couple messages with
  their arrival times

*/

public class Message {
  int arrivalTime = -1;
  String message = "";

  public Message (String message, int arrivalTime) {
    this.message = message;
    this.arrivalTime = arrivalTime;
  }

  public int getTime() { return arrivalTime; }
  public String getMessage() { return message; }
}
