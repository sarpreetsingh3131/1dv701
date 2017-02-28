package tftp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TFTPServer {

	public static final int TFTPPORT = 4970;
	public static final int BUFSIZE = 516;

	// custom address at your PC
	public static final String READDIR = "src/resources/read/";
	public static final String WRITEDIR = "src/resources/write/";

	// OP codes
	public static final int OP_RRQ = 1;
	public static final int OP_WRQ = 2;
	public static final int OP_DAT = 3;
	public static final int OP_ACK = 4;
	public static final int OP_ERR = 5;

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			System.err.printf("usage: java %s\n", TFTPServer.class.getCanonicalName());
			System.exit(1);
		}
		// Starting the server
		try {
			TFTPServer server = new TFTPServer();
			server.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void start() throws IOException {
		byte[] buf = new byte[BUFSIZE];

		// Create socket
		DatagramSocket socket = new DatagramSocket(null);

		// Create local bind point
		SocketAddress localBindPoint = new InetSocketAddress(TFTPPORT);
		socket.bind(localBindPoint);

		System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

		// Loop to handle client requests
		while (true) {

			final InetSocketAddress clientAddress = receiveFrom(socket, buf);

			// If clientAddress is null, an error occurred in receiveFrom()
			if (clientAddress == null)
				continue;

			final StringBuffer requestedFile = new StringBuffer();
			final int reqtype = ParseRQ(buf, requestedFile);

			new Thread() {
				public void run() {
					try {
						DatagramSocket sendSocket = new DatagramSocket(0);

						// Connect to client
						sendSocket.connect(clientAddress);

						System.out.printf("%s request for %s from %s using port\n",
								(reqtype == OP_RRQ) ? "Read" : "Write", clientAddress.getHostName(),
										clientAddress.getPort());

						// Read request
						if (reqtype == OP_RRQ) {

							requestedFile.insert(0, READDIR);

							HandleRQ(sendSocket, requestedFile.toString(), OP_RRQ);
						}

						// Write request
						else {
							requestedFile.insert(0, WRITEDIR);
							HandleRQ(sendSocket, requestedFile.toString(), OP_WRQ);
						}
						sendSocket.close();
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * Reads the first block of data, i.e., the request for an action (read or
	 * write).
	 * 
	 * @param socket
	 *            (socket to read from)
	 * @param buf
	 *            (where to store the read data)
	 * @return socketAddress (the socket address of the client)
	 * @throws IOException
	 */
	private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf) throws IOException {

		// Create datagram packet
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		// Receive packet
		socket.receive(packet);

		// Get client address and port from the packet
		InetSocketAddress socketAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
		return socketAddress;
	}

	/**
	 * Parses the request in buf to retrieve the type of request and
	 * requestedFile
	 * 
	 * @param buf
	 *            (received request)
	 * @param requestedFile
	 *            (name of file to read/write)
	 * @return opcode (request type: RRQ or WRQ)
	 */
	private int ParseRQ(byte[] buf, StringBuffer requestedFile) {

		// See "TFTP Formats" in TFTP specification for the RRQ/WRQ request
		// contents
		ByteBuffer wrap = ByteBuffer.wrap(buf);

		short opcode = wrap.getShort();

		requestedFile.append(new String(buf, 2, buf.length - 2));
		return opcode;
	}

	/**
	 * Handles RRQ and WRQ requests
	 * 
	 * @param sendSocket
	 *            (socket used to send/receive packets)
	 * @param requestedFile
	 *            (name of file to read/write)
	 * @param opcode
	 *            (RRQ or WRQ)
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void HandleRQ(DatagramSocket sendSocket, String requestedFile, int opcode) throws IOException {
		String filePath = requestedFile.split("\0")[0];
		byte[] buffer = new byte[BUFSIZE];
		short block = 0;

		if (opcode == OP_RRQ) {
			// See "TFTP Formats" in TFTP specification for the DATA and ACK
			// packet contents

			/* After the server gets a read request the server should send the first DATA packet
			 * and for each DATA packet sent an ACK packet should be received.
			 * In general the ACK packet contains the block number of the aknowledged DATA packet.
			 */
			++block;
			
			/* Put the opcode and block number in a byte buffer */
			short shortVal = OP_DAT;
			ByteBuffer wrap = ByteBuffer.wrap(buffer);
			wrap.putShort(shortVal);
			wrap.putShort(block);
			
			/* Get the data from requested file and put it in the byte buffer
			 * then create the DATA packet and send it.
			 * Handle exceptions:
			 * 1. file not found
			 * 2. not enough space
			 */
			Path p = Paths.get(filePath);
			byte[] data = new byte[Files.readAllBytes(p).length];
			data = Files.readAllBytes(p);
			wrap.put(data);
			
			/* The last datagram packet needs to be less than 516 bytes to signal the end of transfer
			 * if the file is larger than 511, create for loop to send more DATA packets
			 */
			DatagramPacket DATApacket = new DatagramPacket(wrap.array(), wrap.array().length - 1);
			sendSocket.send(DATApacket);
			
			/* Receive an ACK packet, compare block number with sent block number
			 * if not the same block number, resend (but not in an enternity)
			 */
			byte[] ack = new byte[4];
			DatagramPacket ACKpacket = new DatagramPacket(ack, ack.length);
			sendSocket.receive(ACKpacket);
			wrap = ByteBuffer.wrap(ack);
			opcode = wrap.getShort();
			short blockConfirm = wrap.getShort();
			System.out.println(opcode + " " + blockConfirm);
			
			//boolean result = send_DATA_receive_ACK(sendSocket, opcode, block);
			
			/* if result is false, send error packet */
			try {
				FileInputStream stream = new FileInputStream(filePath);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* After server gets a write request it should send an ACK package in response and
			 * its block number should be equals to zero. And each DATA packet must be aknowledged
			 * by an ACK package before next packet can be sent.
			 * When the final DATA packet has been received we should wait a while after sending the
			 * the final ACK packet to be able to retransmit if it was lost. We will know it was lost
			 * if we receive the final DATA packet again.
			 */
		} else if (opcode == OP_WRQ) {
			block++;
			// boolean result = receive_DATA_send_ACK(params);
		} else {
			System.err.println("Invalid request. Sending an error packet.");
			// See "TFTP Formats" in TFTP specification for the ERROR packet
			// contents
			// send_ERR(params);
			return;
		}
	}

	private boolean send_DATA_receive_ACK(DatagramSocket sendSocket, int block) throws IOException {
		ByteBuffer wrap = ByteBuffer.allocate(4);
		wrap.putShort((short) 4);
		wrap.putShort((short) block);
		DatagramPacket packet = new DatagramPacket(wrap.array(), wrap.array().length);
		System.out.println("hello");
		sendSocket.receive(packet);

		return true;
	}

	// private boolean receive_DATA_send_ACK(params) {return true;}

	// private void send_ERR(DatagramSocket sendSocket, int errorCode, String errorMessage) {}
}