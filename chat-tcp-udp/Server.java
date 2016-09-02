/* =====================================
 *
 *            CHAT TCP/UDP
 *            June - 2016
 *     Author: Gabriel de Lima Rabelo
 *
 *  Server for a chat application that
 *  authenticates clients that Sign-up
 *  for the chat and provides information
 *  about the currently logged in clients
 *  so that the users can talk to each other
 *
 * ===================================== */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Server {
  private static List<String[]> clients = new ArrayList<>();

  public static void main(String[] args) throws Exception {
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
          client = server.accept();
        } catch (IOException e) {
          System.err.println(e.getMessage());
          System.exit(1);
        } finally {
          new Conection(client).start();
        }
      }
    } finally {
      server.close();
    }
  }

  private static class Conection extends Thread {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    String client[] = new String[3];

    public Conection(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        String option;

        while ((option = input.readLine()) != null) {
          if (option.equals("SIGN-UP")) {
            client[0] = input.readLine(); /* EMAIL */
            client[1] = input.readLine(); /* IP */
            client[2] = input.readLine(); /* PORT */

            boolean exists = false;
            for (String[] c : clients) {
              if (c[0].equals(client[0])) {
                output.println("ALREADY EXISTS");
                exists = true;
              }
            }

            if(exists) continue;

            clients.add(client);
            System.out.println("New client: " + client[0] + "\t" + client[1] + "\t" + client[2]);
            output.println("OK");
          }

          else if (option.equals("LIST ALL")) {
            for (String[] c : clients)
            output.println(formatClient(c));

            output.println("#"); /* FIM */
          }

          else if (option.equals("LIST USER")) {
            String email = input.readLine();
            boolean found = false;

            for (String[] c : clients)
            if (c[0].equals(email)) {
              output.println(formatClient(c));
              found = true;
              break;
            }

            if (!found)
            output.println("NOT FOUND");

            output.println("#"); /* The end of the streaming */
          }

          else if (option.equals("EXIT"))
          break;

          else {
            output.println("ERROR");
          }
        }

        int id = clients.indexOf(client);
        clients.remove(id);
        System.out.println("Client " + client[0] + " just left.");

        input.close();
        output.close();
        socket.close();

      } catch (IOException e) {
        System.out.println(e.getMessage());
        int id = clients.indexOf(client);
        clients.remove(id);
        System.out.println("Client " + client[0] + " just left.");
        return;
      }

      return;
    }

    public String formatClient(String[] c) {
      String str;

      str = "\n--------------------";
      str += "\nEMAIL: \t";
      str += c[0];
      str += "\nIP: \t";
      str += c[1];
      str += "\nPORT: \t";
      str += c[2];
      str += "\n--------------------";

      return str;
    }
  }
}
