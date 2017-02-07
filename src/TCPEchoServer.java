import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {

	private final static int PORT = 4950;
	private final static int BUFSIZE = 1024;
	private static int userID = 0;

	public static void main(String[] args) throws IOException {
		// Create server socket
		ServerSocket serverSocket = new ServerSocket(PORT);

		System.out.println("Server Running");

		// do this forever
		while (true) {
			// Wait for connection
			Socket socket = serverSocket.accept();

			// Once connected, create client thread
			ServerThread serverThread = new ServerThread(socket, BUFSIZE, (++userID));

			// execute the thread
			serverThread.start();
		}
	}
}

class ServerThread extends Thread {

	private Socket socket;
	private int bufsize;
	private InputStream inputStream;
	private OutputStream outputStream;
	private int userID;

	public ServerThread(Socket socket, int bufsize, int userID) {
		this.socket = socket;
		this.bufsize = bufsize;
		this.userID = userID;

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
				byte[] buf = new byte[bufsize];

				// read message and save in buffer
				inputStream.read(buf);

				// save message in string and remove empty spaces
				recievedMessage = new String(buf).trim();

				// if message is not empty, sent it back to client
				if (!recievedMessage.isEmpty()) {
					
					// send message
					outputStream.write(recievedMessage.getBytes());
					
					// print user details
					System.out.printf("[User " + userID + "] TCP echo request from %s", socket.getInetAddress());
					System.out.printf(" using port %d", socket.getPort());
					System.out.print(" (Received [" + recievedMessage.length() + "bytes], Sent ["
							+ recievedMessage.length() + " bytes], Buffer size = " + bufsize + ")\n");
				}
			} while (!recievedMessage.isEmpty());

			// at last close the socket
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}