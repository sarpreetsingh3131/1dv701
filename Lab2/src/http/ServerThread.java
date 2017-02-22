package http;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import http.exceptions.BadRequestException;
import http.exceptions.ConflictException;
import http.exceptions.LockedException;
import http.exceptions.ServiceUnavailableException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;
import http.exceptions.VersionNotSupportedException;
import http.response.ResponseFactory;

public class ServerThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private RequestParser requestParser;
	private byte[] buffer;
	private final int clientId;
	private final int TIME_OUT_IN_MS = 50000;

	public ServerThread(Socket socket, int clientId) {
		this.socket = socket;
		requestParser = new RequestParser();
		buffer = new byte[9000];
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
	 * The thread stops whenever any of the exception comes. There is one rare
	 * case and that is when request is PUT, if I don't break it then browser
	 * freeze until it is time out.
	 */
	@Override
	public void run() {

		while (true) {
			try {

				if (Server.UNDER_MAINTAINENCE) {
					throw new ServiceUnavailableException();
				}

				socket.setSoTimeout(TIME_OUT_IN_MS);
				requestParser = requestParser.parse(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				responseFactory.getResponse(requestParser).write();

			} catch (SocketTimeoutException e) {
				responseFactory.writeResponse408RequestTimeout();
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

			} catch (ConflictException e) {
				responseFactory.writeResponse409Conflict(requestParser.getUploadedFileName());
				break;

			} catch (UnsupportedMediaTypeException e) {
				responseFactory.writeResponse415UnsupportedMediaType();
				break;

			} catch (LockedException e) {
				responseFactory.writeResponse423Locked();
				break;

			} catch (UnavailableForLegalReasonsException e) {
				responseFactory.writeResponse451UnavailableForLegalReasons();
				break;

			} catch (FileNotFoundException e) {
				responseFactory.writeResponse404NotFound();
				break;

			} catch (SecurityException e) {
				responseFactory.writeResponse403Forbidden();
				break;

			} catch (IOException e) {
				responseFactory.writeResponse500InternalServerError();
				break;
			}

			if (requestParser.connectionClosed() || requestParser.getMethodType() == Method.MethodType.PUT) {
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