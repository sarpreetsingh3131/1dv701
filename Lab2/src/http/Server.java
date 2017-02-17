package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public final static int PORT = 8080;
	public final static boolean UNDER_MAINTAINENCE = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		// Creates server with port.
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("ERROR: PORT IS IN USE!!");
			System.exit(1);
		}

		// Waits for connections to accept.
		while (true) {
			Socket socket = serverSocket.accept();
			// After accepting a new client thread is created for the client.
			ClientThread client = new ClientThread(socket);
			client.start();
		}
	}
}