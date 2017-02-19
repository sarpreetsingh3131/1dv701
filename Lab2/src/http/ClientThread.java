package http;

import java.io.IOException;
import java.net.Socket;
import http.exceptions.InternalServerException;
import http.exceptions.RequestTimeoutException;
import http.exceptions.ServiceUnavailableException;
import http.exceptions.VersionNotSupportedException;
import http.exceptions.BadRequestException;
import http.response.ResponseFactory;

/*
 * A client thread handling the requests from the client.
 */
public class ClientThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private Request request;
	private byte[] buffer;
	private final int TIMEOUT = 10;

	public ClientThread(Socket socket) {
		this.socket = socket;
		request = new Request();
		buffer = new byte[8192];

		// creating response factory including the shared directory.
		responseFactory = new ResponseFactory(new SharedFolder(), this.socket, this.buffer);
	}

	@Override
	public void run() {
		// A loop processing all the requests made by client
		while (true) {
			try {
				// Maintenance checking for the 503 Response.
				if (Server.UNDER_MAINTAINENCE) {
					throw new ServiceUnavailableException();
				}
				responseFactory.getResponse(request.parseRequest(socket, TIMEOUT)).write();
			} catch (InternalServerException e) {
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
			} catch (RequestTimeoutException e) {
				responseFactory.writeResponse408RequestTimeout();
				break;
			}
		}

		try {
			// closing socket.
			socket.close();
		} catch (IOException e) {
			responseFactory.writeResponse500InternalServerError();
		}
	}
}