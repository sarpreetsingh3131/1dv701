import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPEchoClient extends NetworkLayer {

	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private String MSG = "An Echo Message!";
	private int sentMessages = 0;

	public UDPEchoClient(String[] args) {
		super(args);

		// If needed, add new message here
	}

	// Use thread for running the program 1 second
	@Override
	public void run() {
		for (sentMessages = 0; sentMessages < super.transferRate; sentMessages++) {
			sendAndRecieveMessages();
			delay();
		}
	}

	@Override
	public void runClient() {
		isValidMessage();

		try {
			// Create socket and bind with local point
			socket = new DatagramSocket(null);
			socket.bind(super.localBindPoint);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// Create datagram packet for sending message
		sendPacket = new DatagramPacket(MSG.toString().getBytes(), MSG.length(), super.remoteBindPoint);

		// Create datagram packet for receiving echoed message
		receivePacket = new DatagramPacket(super.buf, super.buf.length);

		super.runForOneSecond(new Thread(this));

		// at last close the socket
		socket.close();

		super.displayTime(sentMessages);
	}

	private void sendAndRecieveMessages() {
		try {
			// send packet
			socket.send(sendPacket);

			// receive packet
			socket.receive(receivePacket);

			// compare send and receive packets
			super.compareSentAndReceivedMessage(MSG,
					new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength()));
		} catch (Exception e) {
			return;
			// catch exception here if time is out and socket is receiving or
			// sending the message
		}
	}

	// Check message size according to UDP packet size
	private void isValidMessage() {
		if (MSG.length() > 65507) {
			System.out.println("Error: Message is too long!!");
			System.exit(1);
		}
		if (MSG.isEmpty()) {
			System.out.println("Error: Message is empty!!");
			System.exit(1);
		}
	}
}