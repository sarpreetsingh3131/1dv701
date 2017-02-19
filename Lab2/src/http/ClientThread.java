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
	private final int TIMEOUT = 100000;
	private final int clientId;

	public ClientThread(Socket socket, int clientId) {
		this.socket = socket;
		request = new Request();
		buffer = new byte[8192];
		this.clientId = clientId;
		responseFactory = new ResponseFactory(this);
	}

	@Override
	public void run() {
		System.out.println("Client " + clientId + " connected");
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
			if(request.connectionClosed()) 
				break;
		}
		
		try {
			// closing socket.
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