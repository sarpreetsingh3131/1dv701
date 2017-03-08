package tftp;

import java.io.*;
import java.net.*;
import tftp.errors.ErrorFactory;
import tftp.exceptions.*;

public class ServerThread {

	private final int BUFSIZE = 516;
	private ErrorFactory errFactory;
	private DatagramSocket sendSocket;

	public void start(int PORT) {

		try {
			DatagramSocket socket = new DatagramSocket(null);
			SocketAddress localBindPoint = new InetSocketAddress(PORT);
			socket.bind(localBindPoint);
			System.out.printf("Listening at port %d for new requests\n", PORT);

			byte[] buf = new byte[BUFSIZE];

			while (true) {

				Request request = Request.parse(socket, buf);
				final InetSocketAddress clientAddress = request.getClientAddress();

				new Thread() {
					@Override
					public void run() {

						try {
							sendSocket = new DatagramSocket(0);
							sendSocket.connect(clientAddress);
							errFactory = new ErrorFactory(sendSocket);
							request.printDetails();

							if (!request.isOctet()) {
								throw new NotDefinedException("Unallowed Mode");
							}

							new RequestHandler().handle(sendSocket, request, sendSocket.getPort());

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
						
						} catch (NoSuchUserException e) {
							errFactory.sendError7NoSuchUser();
						
						} catch (IOException e) {
							e.printStackTrace();
						} 
						
						sendSocket.close();
						System.out.println("Connection Closed");
					}
				}.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}