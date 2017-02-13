package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	final static int PORT = 8080;
	static int clientId = 0;

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(PORT);

		while (true) {
			Socket socket = serverSocket.accept();
			ClientThread client = new ClientThread(socket, ++clientId, new SharedFolder());
			client.start();
		}
	}
}
