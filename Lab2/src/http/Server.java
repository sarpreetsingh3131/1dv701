package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public final static int PORT = 8080;
	public final static boolean UNDER_MAINTAINENCE = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("ERROR: PORT IS IN USE!!");
			System.exit(1);
		}
		
		int clientId = 0;
		while (true) {
			Socket socket = serverSocket.accept();
			ServerThread client = new ServerThread(socket, ++clientId);
			client.start();
		}
	}
}