package tftp;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import tftp.exceptions.*;
import tftp.packets.*;

public class RequestHandler {

	private final int RETRANSMIT_LIMIT = 10;
	private final int DATASIZE = 512;
	private ServerDirectory directory;
	private final int TIMEOUT = 8000;

	public RequestHandler() {
		this.directory = new ServerDirectory();
	}

	/**
	 * This method handle the client request. First it checks if mode is
	 * 'octet', then it pass the request to further methods by checking the type
	 * of request. The '0' inside the readRQ and writeRQ method parameter is the
	 * block number.
	 * 
	 * @param sendSocket
	 * @param requestParser
	 * @throws FileNotFoundException
	 *             If requested file is not found
	 * @throws NotDefinedException
	 *             For sending error code 0
	 * @throws IOException
	 *             If problem comes during sending or receiving the packets
	 * @throws FileAlreadyExistsException
	 *             When client wants to write a file but it already exist
	 * @throws OutOfMemoryException
	 *             When disk space is low
	 * @throws IllegalTFTPOperationException
	 *             When request type is invalid
	 * @throws InvalidTransferIDException
	 *             When received packet port and client's socket port is not
	 *             same
	 * @throws AccessViolationException
	 *             When directory or file does not allow access
	 * @throws NoSuchUserException
	 *             When user
	 * 
	 */
	public void handle(DatagramSocket sendSocket, RequestParser requestParser) throws FileNotFoundException,
			NotDefinedException, IOException, FileAlreadyExistsException, OutOfMemoryException,
			IllegalTFTPOperationException, InvalidTransferIDException, AccessViolationException, NoSuchUserException {

		if (!requestParser.isOctet()) {
			throw new NotDefinedException("Unallowed Mode");
		}

		switch (requestParser.getType()) {

		case INVALID:
			throw new IllegalTFTPOperationException("Invalid Request");

		case READ:
			readRQ(sendSocket, requestParser.getRequestedFile(), 0);
			break;

		case WRITE:
			writeRQ(sendSocket, requestParser.getRequestedFile(), 0);
			break;
		}
	}

	/**
	 * In this method, we write a requested file in the server directory. For
	 * this we first set the file path and send an ACK packet to client. Then we
	 * enter in a loop which runs until limit exceeded or received packet is not
	 * data packet or if received packet is the last packet. Inside the loop we
	 * create a data packet and wait for client response. If timeout occurs we
	 * try again until limit exceeded. Once we get packet we compare the ports
	 * to verify the client. In addition we also verify if received packet is
	 * data packet. If one of the verification failed we delete the file which
	 * is half written and throw exception. Otherwise we continue and write the
	 * data into the file. Why starts from 4? because first four bytes belongs
	 * to packet opcode and block. After writing the data we send ACK packet to
	 * client with same block. Then we look if the received packet is the last
	 * one, if yes we save the file and break the loop otherwise we again repeat
	 * this process.
	 */
	private void writeRQ(DatagramSocket sendSocket, String path, int block)
			throws FileNotFoundException, NotDefinedException, IOException, FileAlreadyExistsException,
			OutOfMemoryException, InvalidTransferIDException, AccessViolationException {

		int timeoutLimit = 0;
		directory.setWritePath(path);
		sendSocket.send(new ACKPacket(block).toDatagramPacket());

		while (true) {

			try {
				Packet dataPacket = new DataPacket();
				sendSocket.setSoTimeout(TIMEOUT);
				sendSocket.receive(dataPacket.toDatagramPacket());

				if (dataPacket.getPort() != sendSocket.getPort()) {
					directory.deleteFile();
					throw new InvalidTransferIDException();
				}

				if (dataPacket.getOpcode() != DataPacket.OP_DAT) {
					directory.deleteFile();
					throw new NotDefinedException("Expected Data Packet");
				}

				int dataLength = directory.write(Arrays.copyOfRange(dataPacket.getData(), 4, dataPacket.getLength()));

				Packet ackPacket = new ACKPacket(dataPacket.getBlock());
				sendSocket.send(ackPacket.toDatagramPacket());

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

	/**
	 * In this method we begin by setting the path of requested file. Then we
	 * enter in a loop which goes on until we reach the limit or we get last
	 * packet or if an error occurs such as got error packet from client. Inside
	 * this loop we create a buffer(512), read the file, increase the block and
	 * create a data packet from it. Then we send this packet and wait for ACK.
	 * If timeout occurs and got no ACK we try again until we reach the limit.
	 * When we get packet from client, we first check if it is from same client
	 * by comparing the port numbers. We throw exception if they are not same.
	 * Furthermore we get opcode and block from received packet and check if its
	 * an error packet, if yes we again throw exception. We do this process
	 * until we limit exceeded or we get ACK packet whose block is same as we
	 * expected. We throw exception if limit exceeded otherwise we break this
	 * loop if packet is less than 512 which means its the last one.
	 * 
	 */
	private void readRQ(DatagramSocket sendSocket, String path, int block) throws FileNotFoundException,
			NotDefinedException, IOException, InvalidTransferIDException, NoSuchUserException {

		int retry = 0;
		short opcode = 0;
		short blockCode = 0;
		directory.setReadPath(path);

		while (true) {

			byte[] buffer = new byte[DATASIZE];
			int bytesRead = directory.read(buffer);
			Packet dataPacket = new DataPacket(++block, buffer, bytesRead);

			do {

				try {
					sendSocket.send(dataPacket.toDatagramPacket());
					Packet ackPacket = new ACKPacket();
					sendSocket.setSoTimeout(TIMEOUT);
					sendSocket.receive(ackPacket.toDatagramPacket());

					if (ackPacket.getPort() != sendSocket.getPort()) {
						throw new InvalidTransferIDException();
					}

					opcode = ackPacket.getOpcode();
					blockCode = ackPacket.getBlock();

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