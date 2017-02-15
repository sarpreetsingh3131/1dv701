import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoServer {

	final static int MYPORT = 4950;
	final static int BUFSIZE = 1024;

	public static void main(String[] args) throws IOException {
		byte[] buf = new byte[BUFSIZE];

		System.out.println("Server Running");
		/* Create socket */
		@SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket(null);

		/* Create local bind point */
		SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
		socket.bind(localBindPoint);
		while (true) {
			/* Create datagram packet for receiving message */
			DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

			/* Receiving message */
			socket.receive(receivePacket);

			// Received message
			String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(),
					receivePacket.getLength());

			/* Create datagram packet for sending message */
			DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
					receivePacket.getAddress(), receivePacket.getPort());

			/* Send message */
			socket.send(sendPacket);

			System.out.printf("UDP echo request from %s", receivePacket.getAddress().getHostAddress());
			System.out.printf(" using port %d", receivePacket.getPort());
			System.out.print(" [Received " + receivedMessage.length() + " bytes] [Sent " + receivedMessage.length()
					+ " bytes] [Buffer size = " + BUFSIZE + "]\n");
		}
	}
}