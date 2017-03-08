package tftp;

import java.io.*;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import tftp.exceptions.*;
import tftp.packets.*;

public class RequestHandler {

	private final int RETRANSMIT_LIMIT = 10;
	private final int DATASIZE = 512;
	private ServerDirectory directory;
	private final int TIMEOUT = 1;

	public RequestHandler() {
		this.directory = new ServerDirectory();
	}

	public void handle(DatagramSocket sendSocket, Request request, int REMOTE_PORT) throws FileNotFoundException,
			NotDefinedException, IOException, FileAlreadyExistsException, OutOfMemoryException,
			IllegalTFTPOperationException, InvalidTransferIDException, AccessViolationException, NoSuchUserException {

		int block = 0;

		switch (request.getType()) {

		case INVALID:
			throw new IllegalTFTPOperationException("Invalid Request");

		case READ:
			readRQ(sendSocket, request.getRequestedFile(), block, REMOTE_PORT);
			break;

		case WRITE:
			writeRQ(sendSocket, request.getRequestedFile(), block, REMOTE_PORT);
			break;
		}
	}

	private void writeRQ(DatagramSocket sendSocket, String path, int block, int REMOTE_PORT)
			throws FileNotFoundException, NotDefinedException, IOException, FileAlreadyExistsException,
			OutOfMemoryException, InvalidTransferIDException, AccessViolationException {

		int timeoutLimit = 0;
		directory.setWritePath(path);
		sendSocket.send(new ACKPacket(block).toDatagramPacket());

		while (true) {

			try {
				Packet data = new DataPacket();
				sendSocket.setSoTimeout(TIMEOUT);
				sendSocket.receive(data.toDatagramPacket());

				if (data.toDatagramPacket().getPort() != REMOTE_PORT) {
					directory.deleteFile();
					throw new InvalidTransferIDException();
				}

				if (data.getOpcode() != DataPacket.OP_DAT) {
					directory.deleteFile();
					throw new NotDefinedException("Expected Data Packet");
				}

				int dataLength = directory.write(data.toDatagramPacket());

				Packet ACK = new ACKPacket(data.getBlock());
				sendSocket.send(ACK.toDatagramPacket());

				if (dataLength < DATASIZE) {
					directory.closeOutputStream();
					break;
				}

			} catch (SocketTimeoutException e) {
				if (++timeoutLimit >= RETRANSMIT_LIMIT) {
					throw new NotDefinedException("No Response From Client");
				}
			}
		}
	}

	private void readRQ(DatagramSocket sendSocket, String path, int block, int REMOTE_PORT)
			throws FileNotFoundException, NotDefinedException, IOException, InvalidTransferIDException,
			NoSuchUserException {

		int retry = 0;
		short opcode = 0;
		short blockCode = 0;
		directory.setReadPath(path);

		while (true) {

			byte[] buffer = new byte[DATASIZE];
			int bytesRead = directory.read(buffer);
			Packet data = new DataPacket(++block, buffer, bytesRead);

			do {
				
				try {
					sendSocket.send(data.toDatagramPacket());
					Packet ACK = new ACKPacket();
					sendSocket.setSoTimeout(TIMEOUT);
					sendSocket.receive(ACK.toDatagramPacket());

					if (ACK.toDatagramPacket().getPort() != REMOTE_PORT) {
						throw new InvalidTransferIDException();
					}

					opcode = ACK.getOpcode();
					blockCode = ACK.getBlock();

					if (opcode == ErrorPacket.OP_ERR) {
						throw new NotDefinedException("Expected ACK Packet");
					}

				} catch (SocketTimeoutException e) {

				}

			} while (opcode != ACKPacket.OP_ACK && blockCode != block && ++retry < RETRANSMIT_LIMIT);

			if (retry >= RETRANSMIT_LIMIT) {
				throw new NotDefinedException("Resend Limit Exceeded");
			}

			else if (bytesRead < DATASIZE) {
				directory.closeInputStream();
				break;
			}
		}
	}
}