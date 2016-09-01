/* =====================================
 *
 *         CLIENT/SERVER EXAMPLE
 *            August - 2016
 *     Author: Gabriel de Lima Rabelo
 *
 *  Simple client for requesting files
 *  from a file server.
 *
 * ===================================== */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.InputStream;

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

      while (loop) {
        /* MENU */
        System.out.println("\n------------------------------");
        System.out.println("1 - List all files");
        System.out.println("2 - Request a file");
        System.out.println("0 - Exit");
        System.out.println("------------------------------\n");

        String op = sc.nextLine();

        switch (op) {
          case "1": listAll(); break;
          case "2": request(); break;
          case "0": output.println("EXIT"); loop = false; break;
          default:
          System.out.println("Opção inválida!");
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

  public static void listAll() throws IOException {
    output.println("LIST"); // Send the request for a list of files
    String confirmation = input.readLine(); // Reads confirmation

    if(confirmation.equals("ERROR")) // If error, print to console
    System.out.println("Error!");

    /* Reads the number of files sent by server */
    int filesQtd = Integer.parseInt(input.readLine());

    for (int i = 0; i < filesQtd; i++)
    System.out.println(input.readLine());
  }

  public static void request() throws IOException {
    output.println("REQUEST"); // Send the request for a single file
    String confirmation = input.readLine(); // Reads confirmation

    if(confirmation.equals("ERROR")) // If error, print to console
    System.out.println("Error!");

    System.out.println("Enter the name of the file desired with the extension. Example: file1.txt");
    String fileName = sc.nextLine();

    /* Send the name of the file desired */
    output.println(fileName);

    InputStream inputStream = socket.getInputStream();
    BufferedOutputStream outputBuffered = new BufferedOutputStream(new FileOutputStream(fileName));

    /* Creates an array of bytes to hold the stream of data */
    byte[] b = new byte[8192];
    int bytesRead = inputStream.read(b, 0, b.length);
    outputBuffered.write(b, 0, bytesRead);
    outputBuffered.close();
  }
}
