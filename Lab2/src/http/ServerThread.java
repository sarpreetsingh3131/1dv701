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

/*
 * A client thread handling the requests from the client.
 */
public class ServerThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private RequestParser request;
	private byte[] buffer;
	private final int clientId;
	private final int TIME_OUT = 10000;
	
	
	public ServerThread(Socket socket, int clientId) {
		this.socket = socket;
		request = new RequestParser();
		buffer = new byte[8000];
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

				socket.setSoTimeout(TIME_OUT);
				request = request.parse(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				responseFactory.getResponse(request).write();

			} catch (SocketTimeoutException e) {
				responseFactory.writeResponse408RequestTimeout();
				break;
			} catch (IOException e) {
				e.printStackTrace();
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
			} catch (ArrayIndexOutOfBoundsException e) {
				responseFactory.writeResponse422UnprocssableEntity();
				break;
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