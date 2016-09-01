import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.OutputStream;

import java.net.*;

public class Server {
  public static Scanner sc;
  public static ServerSocket server;
  public Socket client = null;

  public static void main(String args[]) throws IOException {
    /* Server socket info */
    ServerSocket server = null;
    Socket client = null;
    int port = 8080; // Default port

    while (true) {
      try {
        /* Tries to create a serversocket using the default port at first */
        server = new ServerSocket(port, 5);
        break;
      } catch (IOException e) {
        /* In case the port is already being used, an exception is thrown,
        * it catches it and tries the next port */
        port++;
      }
    }

    System.out.println("Server up and running! \nWaiting for clients at port " + port + "\n");

    try {
      while (true) {
        try {
          /* Waits for a client connection */
          client = server.accept();
        } catch (IOException e) {
          System.err.println(e.getMessage());
          System.exit(1);
        } finally {
          /* Starts a new thread for the new client */
          new Conection(client).start();
        }
      }
    } finally {
      server.close(); // Closes server
    }
  }

  private static class Conection extends Thread {
    private static int clients = 0;
    private int id;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private OutputStream outputStream;
    private BufferedInputStream in;

    public Conection(Socket socket) {
      this.socket = socket;
      this.id = ++clients;
    }

    public void run() {
      System.out.println("Client " + id + " at IP: " + socket.getInetAddress() + " PORT: " + socket.getPort() + " just entered");

      try {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        String option;

        /* Reads input from client */
        while ((option = input.readLine()) != null) {
          if(option.equals("LIST"))
            list();

          else if(option.equals("REQUEST"))
            send();

          else if(option.equals("EXIT"))
            System.out.println("Client " + id + " just left.");

          else
            output.println("ERROR");
        }

        input.close();
        output.close();
        socket.close();
      } catch (IOException e) {
        System.out.println(e.getMessage());
        System.out.println("Client " + id + " just left.");
      }

      return;
    }

    public void list() {
      System.out.println("Client " + id + " requested a list of all files");
      output.println("OK"); // Send simple confirmation

      File folder = new File(".");
      File[] listOfFiles = folder.listFiles();

      output.println(listOfFiles.length);

      for (int i = 0; i < listOfFiles.length; i++)
        output.println(listOfFiles[i].getName());

      output.flush();
    }

    public void send() {
      output.println("OK"); // Send simple confirmation

      try {
        /* Reads the name of the file requested */
        String fileName = input.readLine();
        System.out.println("Client " + id + " requested a file: " + fileName);

        File file = new File(fileName);

        byte[] b = new byte[(int) file.length()];
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        OutputStream os = socket.getOutputStream();

        inputStream.read(b, 0, b.length);
        os.write(b, 0, b.length);
        os.flush();

        inputStream.close();
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
