/* =====================================
 *
 *         CLIENT/SERVER EXAMPLE
 *            August - 2016
 *     Author: Gabriel de Lima Rabelo
 *
 *  Simple server for information on the
 *  2016 Olympics medals scoreboard,
 *  by country.
 *
 * ===================================== */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Server {
	/*
	 * List to hold the information from all countries
	 * from the file 'medals.txt'
   */
	private static List<Country> countries = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("medals.txt"));

		/*
		 * Reads from file, creates an object for each Country
		 * and stores it.
		 */
		String name;
		while ((name = br.readLine()) != null) {
			String abr = br.readLine();
			String gold = br.readLine();
			String silver = br.readLine();
			String bronze = br.readLine();
			String total = br.readLine();

			Country c = new Country(name, abr, gold, silver, bronze, total);
			countries.add(c);
		}

		br.close(); // Closes BufferedReader br

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
					/* Send client every country's info */
					if (option.equals("ALL")) {
						System.out.println("Client " + id + " requested info on every country");
						output.println("OK"); // Send simple confirmation

						/*
						 * First, send the number of countries that will be sent
						 * so the client knows how many inputs to expect
						 */
						 output.println(countries.size());

						/*
						 * Then, send the info on each country serialized.
						 * Serialize is is not native function. It is implemented
						 * on the Country class, and basically turns the attributes
						 * from the object into a single string.
						 */
						for(Country country : countries)
							output.println(country.serialize());
					}

					/* Send client the info on a single country */
					else if (option.equals("ONE")) {
						output.println("OK"); // Send simple confirmation

						/* Reads the name of the country wanted */
						String countryName = input.readLine();
						System.out.println("Client " + id + " requested info on " + countryName);

						boolean found = false;

						/* Finds the country and send the info */
						for(Country country : countries) {
							if(countryName.equals(country.abr)) {
								output.println(country.serialize());
								found = true;
								break;
							}
						}

						if(!found)
							output.println("NOT FOUND"); // Country not found
					}

					else if (option.equals("EXIT")) {
						System.out.println("Client " + id + " just left.");
					}

					else
						output.println("ERROR"); // Send error message
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
	}
}
