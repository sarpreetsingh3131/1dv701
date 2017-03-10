package tftp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class RequestParser {

	public enum RequestType {READ, WRITE, INVALID}
	private final InetSocketAddress CLIENT_ADDRESS;
	private final RequestType TYPE;
	private final String REQUESTED_FILE;
	private final boolean IS_OCTET;
	private final static String MODE = "octet";

	/**
	 * This method parse the initial client request and return a new instance of
	 * {@link RequestParser}. After receiving packet from client we create an
	 * {@link InetSocketAddress} with the help of received packet, ByteBuffer
	 * helps to get request opcode and buffer helps to get requested file name
	 * as well as requested mode. For getting opcode we have used getShort
	 * method which return first two bytes, for getting file name we split the
	 * buffer with '0' byte by starting from index 2 because we already parse
	 * the opcode which is the first two bytes. Also we must reduce the 2 bytes
	 * from buffer length because we use 2 bytes offset otherwise it will go
	 * (516+2) out of index. According the instruction, we know that requested
	 * file name comes before the requested mode.
	 * 
	 * @param socket
	 * @param buf
	 * @return
	 * @throws IOException
	 *             If some problem comes during receiving the packet
	 * @throws IndexOutOfBoundsException
	 *             If request is not in same format as expected
	 */
	public static RequestParser parse(DatagramSocket socket, byte[] buf) throws IOException, IndexOutOfBoundsException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		return new RequestParser(new InetSocketAddress(packet.getAddress(), packet.getPort()),
				getType(ByteBuffer.wrap(buf).getShort()), new String(buf, 2, buf.length - 2).split("\0")[0],
				new String(buf, 2, buf.length - 2).split("\0")[1].toLowerCase().equals(MODE));
	}

	/**
	 * This method print the client details such as port number, request type
	 * and host name
	 */
	public void printDetails() {
		System.out.printf("%s request for %s from %s using port\n", (TYPE == RequestType.READ) ? "Read" : "Write",
				CLIENT_ADDRESS.getHostName(), CLIENT_ADDRESS.getPort());
	}

	/**
	 * This method convert the opcode into request type
	 * 
	 * @param opcode
	 * @return
	 */
	private static RequestType getType(short opcode) {
		switch (opcode) {
		case 1:
			return RequestType.READ;
		case 2:
			return RequestType.WRITE;
		default:
			return RequestType.INVALID;
		}
	}

	/* Constructor and getters */

	private RequestParser(InetSocketAddress clientAddress, RequestType type, String file, boolean isOctet) {
		CLIENT_ADDRESS = clientAddress;
		TYPE = type;
		REQUESTED_FILE = file;
		IS_OCTET = isOctet;
	}

	public RequestType getType() {
		return TYPE;
	}

	public String getRequestedFile() {
		return REQUESTED_FILE;
	}

	public boolean isOctet() {
		return IS_OCTET;
	}

	public InetSocketAddress getClientAddress() {
		return CLIENT_ADDRESS;
	}
}