/* =====================================
 *
 *            CHAT TCP/UDP
 *            June - 2016
 *     Author: Gabriel de Lima Rabelo
 *
 *  Client for a chat application that
 *  communicates to a server via TCP and
 *  to other clients via UDP
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
  public static DatagramSocket socketUDP;

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

    /* Create server to receive messages via UDP */
    ServerUDP server = new ServerUDP();
    server.start();

    socketUDP = server.getSocket();
    InetAddress ipUDP = server.getIp();
    int portUDP = server.getPorta();

    /* Send messages via TCP to server and via UDP to other clients */
    try {
      /* Creates a client socket */
      socket = new Socket(serverHost, serverPort);
      output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      sc = new Scanner(System.in);

      /* Signing up on the server */
      output.println("SIGN-UP");
      System.out.println("Enter an email: ");

      while (true) {
        String email = sc.nextLine();

        output.println(email);
        output.println(ipUDP.getHostAddress().toString());
        output.println(portUDP);

        String response = input.readLine();

        if (response.equals("ALREADY EXISTS"))
        System.out.println("\n" + email + " is already being used. Sign-up with another email: ");

        else if(response.equals("OK")) {
          System.out.println("\nSuccessfully signed-up.");
          break;
        }

        else
        System.out.println("\nError! Try again: ");
      }

      while (true) {
        System.out.println("\n**************   MENU   ****************");
        System.out.println("1 - List all users from server");
        System.out.println("2 - Request information about a user");
        System.out.println("3 - Send message to a user");
        System.out.println("0 - Exit");
        System.out.println("****************************************\n");
        System.out.println("Enter the menu item: ");

        String op = sc.nextLine();

        if (op.equals("1"))
        listUsers();
        else if (op.equals("2"))
        listUser();
        else if (op.equals("3"))
        sendUDP();
        else if (op.equals("0")) {
          output.println("EXIT");
          break;
        } else
        System.out.println("Invalid Option!");
      }

      System.out.println("You've exited the system.");

      sc.close();
      input.close();
      output.close();
      socket.close();
      socketUDP.close();
      server.setCloseServer(true);

    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void listUsers() {
    System.out.println("Listing all users from server ...");

    try {
      output.println("LIST ALL");

      String response = "";
      while (!(response = input.readLine()).equals("#"))
      System.out.println(response);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void listUser() {
    System.out.println("Enter the email of the user you want more information:");
    String email = sc.nextLine();

    try {
      output.println("LIST USER");
      output.println(email);

      String response = "";
      while (!(response = input.readLine()).equals("#")) {
        if(response.equals("NOT FOUND"))
        System.out.println("\nUser not found.");
        else
        System.out.println(response);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void sendUDP() {
    System.out.println("Enter user's IP:");
    String ipStr = sc.nextLine();

    System.out.println("Enter user's port:");
    int port = sc.nextInt();
    sc.nextLine();

    System.out.println("Enter the message:");
    String message = sc.nextLine();

    try {
      /* ENVIO DA MENSAGEM */
      InetAddress ip = InetAddress.getByName(ipStr);
      DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
      socketUDP.send(packet);
    } catch (SocketException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

class ServerUDP extends Thread {
  private int port = 55555;
  private InetAddress ip;
  private DatagramSocket socket;
  private boolean closeServer;

  public ServerUDP() {
    closeServer = false;

    while (true) {
      try {
        ip = InetAddress.getLocalHost();
        socket = new DatagramSocket(port, ip);
        break;
      } catch (IOException e) {
        this.port++;
      }
    }
  }

  public void run() {
    try {
      byte[] data = new byte[2048];
      while (!closeServer) {
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        String message = new String(packet.getData());
        InetAddress ip = packet.getAddress();
        int port = packet.getPort();
        System.out.println("\n\n**************************************");
        System.out.println("**** YOU HAVE RECEIVED A MESSAGE *****");
        System.out.println("**************************************");
        System.out.println("IP:   \t" + ip + "\nPORT:   " + port + "\nMESSAGE: " + message);
        System.out.println("**************************************\n");

        System.out.println("Enter the menu item: ");
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } finally {
      socket.close();
    }
  }

  public InetAddress getIp() {
    return this.ip;
  }

  public int getPorta() {
    return this.port;
  }

  public DatagramSocket getSocket() {
    return this.socket;
  }

  public void setCloseServer(boolean closeServer) {
    this.closeServer = closeServer;
  }
}
