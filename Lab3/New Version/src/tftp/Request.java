package tftp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Request {

	public enum RequestType {READ, WRITE, INVALID}
	private final InetSocketAddress CLIENT_ADDRESS;
	private final RequestType TYPE;
	private final String REQUESTED_FILE;
	private final boolean IS_OCTET;
	private final static String MODE = "octet";

	public static Request parse(DatagramSocket socket, byte[] buf) throws IOException, ArrayIndexOutOfBoundsException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		ByteBuffer wrap = ByteBuffer.wrap(buf);

		return new Request(new InetSocketAddress(packet.getAddress(), packet.getPort()), getType(wrap.getShort()),
				new String(buf, 2, buf.length - 2).split("\0")[0],
				new String(buf, 2, buf.length - 2).split("\0")[1].equals(MODE));
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

	public void printDetails() {
		System.out.printf("%s request for %s from %s using port\n", (TYPE == RequestType.READ) ? "Read" : "Write",
				CLIENT_ADDRESS.getHostName(), CLIENT_ADDRESS.getPort());
	}

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

	private Request(InetSocketAddress clientAddress, RequestType type, String file, boolean isOctet) {
		CLIENT_ADDRESS = clientAddress;
		TYPE = type;
		REQUESTED_FILE = file;
		IS_OCTET = isOctet;
	}
}