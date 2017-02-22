package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	// Change it true during maintenance
	public final static boolean UNDER_MAINTAINENCE = false;
	public final static int PORT = 8080;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("ERROR: PORT IS IN USE!!");
			System.exit(1);
		}
		
		System.out.println("Server  Started ::::: Under Maintenance = " + UNDER_MAINTAINENCE);

		int clientId = 0;
		while (true) {
			Socket socket = serverSocket.accept();
			ServerThread client = new ServerThread(socket, ++clientId);
			client.start();
		}
	}
}