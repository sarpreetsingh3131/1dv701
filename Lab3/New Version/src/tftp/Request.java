package tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import tftp.exceptions.FileAlreadyExistsException;
import tftp.exceptions.InvalidModeException;
import tftp.exceptions.NotDefinedException;
import tftp.exceptions.OutOfMemoryException;
import tftp.exceptions.ResendLimitExceedException;
import tftp.exceptions.UnknownRequestException;
import tftp.packets.ACKPacket;
import tftp.packets.DataPacket;
import tftp.packets.ErrorPacket;
import tftp.packets.Packet;

public class Request {

	public enum RequestType {READ, WRITE}
	private String requestedFile;
	private final String MODE = "octet";
	private final int RESEND_LIMIT = 10;

	public InetSocketAddress getClientAddress(DatagramSocket socket, byte[] buf) throws IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		return new InetSocketAddress(packet.getAddress(), packet.getPort());
	}

	public RequestType parseRQ(byte[] buf)
			throws InvalidModeException, UnknownRequestException, ArrayIndexOutOfBoundsException {

		ByteBuffer wrap = ByteBuffer.wrap(buf);
		requestedFile = new String(buf, 2, buf.length - 2).split("\0")[0];

		if (!new String(buf, 2, buf.length - 2).split("\0")[1].equals(MODE)) {
			throw new InvalidModeException();
		}

		switch (wrap.getShort()) {
		case 1:		return RequestType.READ;
		case 2:		return RequestType.WRITE;
		default:	throw new UnknownRequestException();
		}
	}

	public void handleRQ(DatagramSocket sendSocket, RequestType reqType, SharedFolder sharedFolder, int DATASIZE)
			throws FileNotFoundException, NotDefinedException, IOException, FileAlreadyExistsException,
			ResendLimitExceedException, OutOfMemoryException {

		int block = 0;
		switch (reqType) {
		case READ: 	readRQ(sendSocket, reqType, sharedFolder, DATASIZE, block);		break;
		case WRITE: writeRQ(sendSocket, reqType, sharedFolder, DATASIZE, block);	break;
		}
	}

	private void writeRQ(DatagramSocket sendSocket, RequestType reqType, SharedFolder sharedFolder, int DATASIZE,
			int block) throws FileNotFoundException, NotDefinedException, IOException, FileAlreadyExistsException, OutOfMemoryException {

		sharedFolder.setWritePath(requestedFile);
		sendSocket.send(new ACKPacket(block).toDatagramPacket());

		while (true) {
			Packet data = new DataPacket();
			sendSocket.receive(data.toDatagramPacket());

			if (data.getOpcode() == DataPacket.OP_DAT) {
				int dataLength = sharedFolder.write(data.toDatagramPacket());

				Packet ACK = new ACKPacket(data.getBlock());
				sendSocket.send(ACK.toDatagramPacket());

				if (dataLength < DATASIZE) {
					return;
				}
			} else {
				System.out.println("RECEIVED OPCODE IS NOT OP_DAT");
			}
		}
	}

	private void readRQ(DatagramSocket sendSocket, RequestType reqType, SharedFolder sharedFolder, int DATASIZE,
			int block) throws FileNotFoundException, NotDefinedException, IOException, ResendLimitExceedException {

		int retry = 0;
		sharedFolder.setReadPath(requestedFile);

		while (true) {
			short opcode;
			short blockCode;
			byte[] buffer = new byte[DATASIZE];
			int bytesRead = sharedFolder.read(buffer);
			Packet data = new DataPacket(++block, buffer, bytesRead);

			do {
				sendSocket.send(data.toDatagramPacket());
				Packet ACK = new ACKPacket();
				sendSocket.receive(ACK.toDatagramPacket());

				opcode = ACK.getOpcode();
				blockCode = ACK.getBlock();

				if (opcode == ErrorPacket.OP_ERR) {
					throw new NotDefinedException();
				}

			} while (opcode != ACKPacket.OP_ACK && blockCode != block && ++retry < RESEND_LIMIT);

			if (retry >= RESEND_LIMIT) {
				throw new ResendLimitExceedException();
			}

			else if (bytesRead < DATASIZE) {
				return;
			}
		}
	}
}