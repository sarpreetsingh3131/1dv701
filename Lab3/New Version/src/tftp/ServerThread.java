package tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import tftp.Request.RequestType;
import tftp.errors.ErrorFactory;
import tftp.exceptions.FileAlreadyExistsException;
import tftp.exceptions.InvalidModeException;
import tftp.exceptions.NotDefinedException;
import tftp.exceptions.OutOfMemoryException;
import tftp.exceptions.ResendLimitExceedException;
import tftp.exceptions.UnknownRequestException;

public class ServerThread extends Thread {

	private final int BUFSIZE = 516;
	private final int DATASIZE = BUFSIZE - 4;
	private final int TIMEOUT = 8000;
	private final int PORT;
	private DatagramSocket socket;
	private SharedFolder sharedFolder;
	private ErrorFactory errFactory;

	public ServerThread(int port) {
		this.PORT = port;
		sharedFolder = new SharedFolder();
	}

	private void setUpSocket() throws SocketException {
		socket = new DatagramSocket(null);
		SocketAddress localBindPoint = new InetSocketAddress(PORT);
		socket.bind(localBindPoint);
		System.out.printf("Listening at port %d for new requests\n", PORT);
	}

	@Override
	public void run() {
		try {
			byte[] buf = new byte[BUFSIZE];
			setUpSocket();

			while (true) {
				Request request = new Request();
				final InetSocketAddress clientAddress = request.getClientAddress(socket, buf);
				RequestType reqType = request.parseRQ(buf);

				DatagramSocket sendSocket = new DatagramSocket(0);
				sendSocket.connect(clientAddress);
				errFactory = new ErrorFactory(sendSocket);
				printDetails(reqType, clientAddress);

				sendSocket.setSoTimeout(TIMEOUT);
				request.handleRQ(sendSocket, reqType, sharedFolder, DATASIZE);
				sendSocket.close();
			}

		} catch (SocketTimeoutException e) {
			e.printStackTrace();

		} catch (InvalidModeException e) {
			e.printStackTrace();

		} catch (UnknownRequestException | ArrayIndexOutOfBoundsException e) {
			errFactory.sendError4IllegalTFTPOperation();

		} catch (FileNotFoundException e) {
			errFactory.sendError1FileNotFound();

		} catch (NotDefinedException e) {
			e.printStackTrace();

		} catch (IOException e) {
			errFactory.sendError2AccessViolation();

		} catch (FileAlreadyExistsException e) {
			errFactory.sendError6FileAlreadyExists();

		} catch (ResendLimitExceedException e) {
			e.printStackTrace();

		} catch (OutOfMemoryException e) {
			errFactory.sendError3DiskFullOrAllocationExceeded();
		}
	}

	private void printDetails(RequestType reqType, InetSocketAddress clientAddress) {
		System.out.printf("%s request for %s from %s using port\n", (reqType == RequestType.READ) ? "Read" : "Write",
				clientAddress.getHostName(), clientAddress.getPort());
	}
}