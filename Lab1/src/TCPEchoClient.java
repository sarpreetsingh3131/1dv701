import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPEchoClient extends NetworkLayer {

	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String MSG = "An Echo Message!";
	private int sentMessages = 0;

	public TCPEchoClient(String[] args) {
		super(args);
		
		//If needed, add new message here
	}

	// Use thread for running the program 1 second
	@Override
	public void run() {
		for (sentMessages = 0; sentMessages < super.transferRate; sentMessages++) {
			sendAndRecieveMessages();
			super.delay();
		}
	}

	@Override
	public void runClient() {
		isValidMessage();

		try {
			// create socket and connect
			socket = new Socket();
			socket.bind(super.localBindPoint);
			socket.connect(super.remoteBindPoint);

			// create input stream for reading message
			inputStream = new DataInputStream(socket.getInputStream());

			// create output stream for sending message
			outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			System.out.println("Error: Server is not running!!");
			System.exit(1);
		}

		super.runForOneSecond(new Thread(this));

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.displayTime(sentMessages);
	}

	private void sendAndRecieveMessages() {
		try {
			// send message
			outputStream.write(MSG.getBytes());

			String receivedMessage = "";

			// do this process until input stream gets byte
			do {
				// create buffer
				super.buf = new byte[super.bufSize];

				// read message and save in array and get no. of read bytes
				int bytesRead = inputStream.read(super.buf);

				// Here add the message into existing one to get complete
				// message even if buffer size is small
				receivedMessage += new String(super.buf, 0, bytesRead);

			} while (receivedMessage.length() < MSG.length());

			// Compare sent and received message, remove white spaces if needed
			super.compareSentAndReceivedMessage(MSG, receivedMessage.trim());
		} catch (Exception e) {
			return;
			// catch exception here if time is out and stream is reading or
			// sending the message
		}
	}

	private void isValidMessage() {
		if (MSG.isEmpty()) {
			System.out.println("Error: Message is empty!!");
			System.exit(1);
		}
	}
}