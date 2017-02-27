package tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Server {

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			System.err.printf("usage: java %s\n", Server.class.getCanonicalName());
			System.exit(1);
		}

		ServerThread serverThread = new ServerThread();
		serverThread.setUpSocket();

		while (true) {
			serverThread.setUpClient();
			serverThread.start();
		}
	}
}

class ServerThread extends Thread {

	private final int TFTPPORT = 4970;
	private final int BUFSIZE = 516;
	private final String READDIR = "src/read/";
	private final String WRITEDIR = "src/write/";
	private final int OP_RRQ = 1;
	private final int OP_WRQ = 2;
	private final int OP_DAT = 3;
	private final int OP_ACK = 4;
	private final int OP_ERR = 5;
	private DatagramSocket socket;
	private InetSocketAddress clientAddress;
	private StringBuffer requestedFile;
	private int reqtype;
	private byte[] buf = new byte[BUFSIZE];

	@Override
	public void run() {
		try {
			DatagramSocket sendSocket = new DatagramSocket(0);
			sendSocket.connect(clientAddress);

			System.out.printf("%s request for %s from %s using port\n", (reqtype == OP_RRQ) ? "Read" : "Write",
					clientAddress.getHostName(), clientAddress.getPort());

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
			e.printStackTrace();
		}
	}

	public void setUpSocket() {
		try {
			socket = new DatagramSocket(null);
			SocketAddress localBindPoint = new InetSocketAddress(TFTPPORT);
			socket.bind(localBindPoint);
			System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void setUpClient() {
		clientAddress = receiveFrom(socket, buf);

		if (clientAddress == null) {
			System.out.println("CLIENT ADDRESS IS NULL");
			System.exit(1);
		}

		requestedFile = new StringBuffer();
		reqtype = ParseRQ(buf, requestedFile);
	}

	private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf) {
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			InetSocketAddress socketAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
			return socketAddress;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private int ParseRQ(byte[] buf, StringBuffer requestedFile) {
		ByteBuffer wrap = ByteBuffer.wrap(buf);
		short opcode = wrap.getShort();
		requestedFile.append(new String(buf, 2, buf.length - 2));
		return opcode;
	}

	private void HandleRQ(DatagramSocket sendSocket, String requestedFile, int opcode) throws IOException {
		String fileName = requestedFile.split("\0")[0];
		System.out.println(
				"Requested path: " + fileName + "\t\tOpcode: " + opcode + "\t\tMode: " + requestedFile.split("\0")[1]);

		byte[] buffer = new byte[BUFSIZE - 4];
		int block = 0;

		if (opcode == OP_RRQ) {

			while (true) {
				++block;
				sendPacket(sendSocket, BUFSIZE, opcode, block, buffer);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				DatagramPacket packet = receivePacket(sendSocket, new byte[BUFSIZE]);
				short ackOpcode = receiveACKOpcode(packet);
				System.out.println("Received ACK Opcode: " + ackOpcode);

				int bytesRead = readFileInBytes(buffer, fileName);
				if (!isValidACK(bytesRead)) {
					break;
				}
			}

		}

		else if (opcode == OP_WRQ) {
			while (!writeFile(fileName, buffer, block, sendSocket)) {
				block++;
			}

		} else {
			System.err.println("Invalid request. Sending an error packet.");
			return;
		}
	}

	private boolean writeFile(String fileName, byte[] buf, int block, DatagramSocket clientSocket) throws IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		sendPacket(clientSocket, OP_ACK, OP_ACK, block, buf);
		clientSocket.receive(packet);
		FileOutputStream stream = new FileOutputStream(fileName, true);
		stream.write(packet.getData(), OP_ACK, packet.getLength() - OP_ACK);

		return packet.getLength() < 512 ? true : false;
	}

	private void sendPacket(DatagramSocket clientSocket, int capacity, int opcode, int block, byte[] buffer) {
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
			byteBuffer.putShort((short) opcode);
			byteBuffer.putShort((short) block);

			if (buffer != null) {
				byteBuffer.put(buffer);
			}

			DatagramPacket packet = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length);
			clientSocket.send(packet);
			System.out.println("Packet is sent!!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DatagramPacket receivePacket(DatagramSocket clientSocket, byte[] buffer) {
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			clientSocket.receive(packet);
			System.out.println("Packet is received!!");
			return packet;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private short receiveACKOpcode(DatagramPacket packet) {
		return ByteBuffer.wrap(packet.getData()).getShort();
	}

	@SuppressWarnings("resource")
	private int readFileInBytes(byte[] buffer, String fileName) {
		try {
			return new FileInputStream(new File(fileName)).read(buffer);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	private boolean isValidACK(int bytesRead) {
		return bytesRead < 512;
	}
}