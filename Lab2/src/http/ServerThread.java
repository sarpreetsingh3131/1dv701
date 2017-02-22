package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import http.exceptions.ServiceUnavailableException;
import http.exceptions.VersionNotSupportedException;
import http.exceptions.BadRequestException;
import http.response.ResponseFactory;

public class ServerThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private RequestParser request;
	private byte[] buffer;
	private final int clientId;
	private final int TIME_OUT_IN_MS = 10000;

	public ServerThread(Socket socket, int clientId) {
		this.socket = socket;
		request = new RequestParser();
		buffer = new byte[8000];
		this.clientId = clientId;
		responseFactory = new ResponseFactory(this);
		System.out.println("Client " + clientId + " connected");
	}

	/**
	 * This method runs until one of Error response comes or connection property
	 * in request header is close. It sets the time out time first and then
	 * parse the client request. Once the request is parsed it goes to response
	 * factory which will return an appropriate response. While parsing if any
	 * error comes it will be catch here and display to client. Server
	 * 'UNDER_MAINTAINENCE' property can be changed manually in Server class.
	 */
	@Override
	public void run() {

		while (true) {
			try {

				if (Server.UNDER_MAINTAINENCE) {
					throw new ServiceUnavailableException();
				}

				socket.setSoTimeout(TIME_OUT_IN_MS);
				request = request.parse(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				responseFactory.getResponse(request).write();

			} catch (SocketTimeoutException e) {
				responseFactory.writeResponse408RequestTimeout();
				break;

			} catch (IOException e) {
				responseFactory.writeResponse500InternalServerError();
				break;

			} catch (BadRequestException e) {
				responseFactory.writeResponse400BadRequest();
				break;

			} catch (VersionNotSupportedException e) {
				responseFactory.writeResponse505HTTPVersionNotSupported();
				break;

			} catch (ServiceUnavailableException e) {
				responseFactory.writeResponse503ServiceUnavailable();
				break;

			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				responseFactory.writeResponse422UnprocssableEntity();
				break;
			}

			if (request.connectionClosed()) {
				break;
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			responseFactory.writeResponse500InternalServerError();
		}

		System.out.println("Client " + clientId + " disconnected");
	}

	public Socket getSocket() {
		return socket;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getClientId() {
		return clientId;
	}
}