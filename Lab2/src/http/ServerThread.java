package http;

import java.io.IOException;
import java.net.Socket;
import http.exceptions.InternalServerException;
import http.exceptions.RequestEntityTooLargeException;
import http.exceptions.RequestTimeoutException;
import http.exceptions.ServiceUnavailableException;
import http.exceptions.VersionNotSupportedException;
import http.exceptions.BadRequestException;
import http.response.ResponseFactory;

/*
 * A client thread handling the requests from the client.
 */
public class ServerThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private Request request;
	private byte[] buffer;
	private final int TIMEOUT = 5000;
	private final int clientId;

	public ServerThread(Socket socket, int clientId) {
		this.socket = socket;
		request = new Request();
		buffer = new byte[1024];
		this.clientId = clientId;
		responseFactory = new ResponseFactory(this);
	}

	@Override
	public void run() {
		System.out.println("Client " + clientId + " connected");

		while (true) {
			try {
				if (Server.UNDER_MAINTAINENCE) {
					throw new ServiceUnavailableException();
				}
				responseFactory.getResponse(request.parseRequest(socket, TIMEOUT)).write();
				
			} catch (IOException | InternalServerException e) {
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
			} catch (RequestEntityTooLargeException e) {
				responseFactory.writeResponse413RequestEntityTooLarge();
			}
			if (request.connectionClosed()) {
				break;
			}
		}
		
		
		try {
			socket.close();
			System.out.println("Client " + clientId + " disconnected");
		} catch (IOException e) {
			responseFactory.writeResponse500InternalServerError();
		}
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