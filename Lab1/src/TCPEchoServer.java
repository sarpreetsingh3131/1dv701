import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {

	final static int PORT = 4950;
	static int userId = 0;

	public static void main(String[] args) throws IOException {
		// Create server socket
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server Running");

		// do this forever
		while (true) {
			// Wait for connection
			Socket socket = serverSocket.accept();

			// Once connected, create client thread
			ClientThread serverThread = new ClientThread(socket, ++userId);

			// execute the thread
			serverThread.start();
		}
	}
}

class ClientThread extends Thread {

	private final int BUFSIZE = 1024;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private final int userId;

	public ClientThread(Socket socket, int userId) {
		this.socket = socket;
		this.userId = userId;

		try {
			// create input stream for reading message
			inputStream = new DataInputStream(this.socket.getInputStream());

			// create output stream for sending message
			outputStream = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			// create message string
			String recievedMessage = "";

			// do this, until received message is empty
			do {
				// create buffer
				byte[] buf = new byte[BUFSIZE];

				// read message and save in buffer
				inputStream.read(buf);

				// save message in string and remove empty spaces
				recievedMessage = new String(buf).trim();

				// if message is not empty, sent it back to client
				if (!recievedMessage.isEmpty()) {
					
					System.out.println(recievedMessage);
					// send message
					outputStream.write(recievedMessage.getBytes());

					// print user details
					System.out.println("[User " + userId + "] [IP " + socket.getInetAddress() + "] [PORT "
							+ socket.getPort() + "] [Received " + recievedMessage.length() + " bytes] [Sent "
							+ recievedMessage.length() + " bytes] [Buffer size = " + BUFSIZE + "]\n");
				}
			} while (!recievedMessage.isEmpty());

			// at last close the socket
			close();
		} catch (Exception e) {
			close();
			// catch exception, when time is out and client socket is closed
		}
		System.out.println("********** [User " + userId + "] connection closed!! ***************\n");
	}

	private void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}