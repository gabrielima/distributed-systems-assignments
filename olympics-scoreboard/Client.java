/* =====================================
 *
 *         CLIENT/SERVER EXAMPLE
 *            August - 2016
 *     Author: Gabriel de Lima Rabelo
 *
 *  Simple client for information on the
 *  2016 Olympics medals scoreboard, for
 *  every country and by each country.
 *
 * ===================================== */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.net.*;

public class Client {
  public static Scanner sc;
  public static Socket socket;
  public static PrintWriter output;
  public static BufferedReader input;

  public static void main(String[] args) {
    String serverHost = null;
    int serverPort = 0;

    /* Check if parameters are correct */
    if (args.length == 2) {
      serverHost = args[0];
      serverPort = Integer.parseInt(args[1]);
    }

    else {
      System.out.println("Correct use: java Client [host name] [port]\nExample:java Client localhost 8080");
      System.exit(1);
    }

    try {
      /* Creates a client socket */
      socket = new Socket(serverHost, serverPort);
      output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      sc = new Scanner(System.in);
      boolean loop = true;

      while(loop) {
        /* MENU */
        System.out.println("\n------------------------------");
        System.out.println("1 - Request information for one country");
        System.out.println("2 - Request information for every country");
        System.out.println("0 - Exit");
        System.out.println("------------------------------\n");

        String op = sc.nextLine();

        switch(op) {
          case "1": getCountry(); break;
          case "2": getAll(); break;
          case "0": output.println("EXIT"); loop = false; break;
          default: System.out.println("Invalid Option!");
        }
      }

      sc.close();
      input.close();
      output.close();
      socket.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void getCountry() throws IOException {
    output.println("ONE"); // Sends request for info on one country
    String confirmation = input.readLine(); // Reads confirmation

    if(confirmation.equals("ERROR")) // If error, print to console
      System.out.println("Error!");

    System.out.println("Enter the abreviation of the desired country all caps. Example: BRA ");
    String countryAbr = sc.nextLine();

    /* Sends the desired country's abreviation */
    output.println(countryAbr);

    /* Reads country's info */
    String data = input.readLine();

    if(data.equals("NOT FOUND"))  // If not found, the user sent the wrong abreviation
      System.out.println("Country not found! Check if you entered the right abreviation.");

    else /* Unserialize string */
      unserialize(data);
  }

  public static void getAll() throws IOException {
    output.println("ALL"); // Sends request for info on every country
    String confirmation = input.readLine(); // Reads confirmation

    if(confirmation.equals("ERROR")) // If error, print to console
      System.out.println("Error!");

    String data = "";

    /* Reads the number of countries sent by server */
    int countryQtd = Integer.parseInt(input.readLine());

    for(int i = 0; i < countryQtd; i++)
      data += (input.readLine() + "\n"); // Concatenate info from every country into a single string

    /* Unserialize string */
    unserialize(data);
  }

  /*
  * Unserializes data from server.
  * Server sents data from each country as a single string.
  * This function parses the string, format it as a table and
  * prints to console.
  */
  public static void unserialize(String data) {
    System.out.print("\n");
    System.out.format("%-25s%-10s%-10s%-10s%-10s%-10s", "NAME", "ABREV", "GOLD", "SILVER", "BRONZE", "TOTAL");
    System.out.print("\n");

    String[] countries = data.split("\n");
    for(String country : countries) {
      String[] c = country.split(";");
      System.out.format("%-25s%-10s%-10s%-10s%-10s%-10s", c[0], c[1], c[2], c[3], c[4], c[5]);
      System.out.print("\n");
    }
  }

}
