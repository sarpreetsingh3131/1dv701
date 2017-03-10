package tftp;

import java.io.*;
import java.net.*;
import tftp.errors.ErrorFactory;
import tftp.exceptions.*;

public class ServerThread {

	private final int BUFSIZE = 516;
	private ErrorFactory errFactory;
	private DatagramSocket sendSocket;

	/**
	 * This method set the socket and local point and connect them. Then it
	 * parse the initial request which return clientAddress. Furthermore, it
	 * create a thread for each client. Inside the thread, it creates a
	 * DatagramSocket with any available port and connect it to clientAddress.
	 * Now this socket will be used for transferring the data between server and
	 * client. In addition, we pass this socket to error factory for sending the
	 * errors when occurs. At last we pass the requestParser and socket to
	 * RequestHandler which then reply to client. If any error occurs we catch
	 * and send appropriate error code to client. We close the connection in the
	 * end.
	 * 
	 * @param PORT
	 */
	public void start(int PORT) {

		try {
			DatagramSocket socket = new DatagramSocket(null);
			SocketAddress localBindPoint = new InetSocketAddress(PORT);
			socket.bind(localBindPoint);
			System.out.printf("Listening at port %d for new requests\n", PORT);

			byte[] buf = new byte[BUFSIZE];

			while (true) {

				RequestParser requestParser = RequestParser.parse(socket, buf);
				final InetSocketAddress clientAddress = requestParser.getClientAddress();

				new Thread() {
					@Override
					public void run() {

						try {
							sendSocket = new DatagramSocket(0);
							sendSocket.connect(clientAddress);
							errFactory = new ErrorFactory(sendSocket);
							requestParser.printDetails();
							new RequestHandler().handle(sendSocket, requestParser);

						} catch (FileNotFoundException e) {
							errFactory.sendError1FileNotFound();

						} catch (NotDefinedException e) {
							errFactory.sendError0NotDefined(e.getMsg());

						} catch (FileAlreadyExistsException e) {
							errFactory.sendError6FileAlreadyExists();

						} catch (OutOfMemoryException e) {
							errFactory.sendError3DiskFullOrAllocationExceeded();

						} catch (IllegalTFTPOperationException e) {
							errFactory.sendError4IllegalTFTPOperation();

						} catch (InvalidTransferIDException e) {
							errFactory.sendError5UnknownTransferID();

						} catch (AccessViolationException e) {
							errFactory.sendError2AccessViolation();

						} catch (IndexOutOfBoundsException e) {
							errFactory.sendError0NotDefined("Malformed Request");

						} catch (IOException e) {
							errFactory.sendError0NotDefined("Internal Error");
						
						} catch (NoSuchUserException e) {
							errFactory.sendError7NoSuchUser();
						}

						sendSocket.close();
						System.out.println("Connection Closed");
					}
				}.start();
			}
		} catch (IOException e) {
			System.out.println("The given port is already in use!!");
			System.exit(1);
		}
	}
}
