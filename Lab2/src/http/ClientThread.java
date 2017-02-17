package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import http.exceptions.InternalServerException;
import http.exceptions.RequestTimeoutException;
import http.exceptions.ServiceUnavailableException;
import http.exceptions.VersionNotSupportedException;
import http.Request.Type;
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
	private final int TIMEOUT = 1000;

	public ClientThread(Socket socket) {
		this.socket = socket;
		request = new Request();
		buffer = new byte[8192];

		// creating response factory including the shared directory.
		responseFactory = new ResponseFactory(new SharedFolder(), this.socket, this.buffer);
	}

	@Override
	public void run() {

		
		 // A loop processing all the requests made by client. Catches exceptions
		 // and returns corresponding response.
		while (true) {
			try {
				// Timeout for the 408 Response.
				socket.setSoTimeout(TIMEOUT);

				// Maintenance checking for the 503 Response.
				if (Server.UNDER_MAINTAINENCE) {
					throw new ServiceUnavailableException();
				}

				// Read and parse the request
				String str = readRequest();
				request = request.parseRequest(str);

				if (request.getRequestType() == Type.POST) {

					// TODO
					// read again for the content body?
				}
				// write the corresponding response.
				responseFactory.getResponse(request).write();

			} catch (InternalServerException e) {
				responseFactory.writeResponse500InternalServerError();
				break;
			} catch (BadRequestException e) {
				responseFactory.writeResponse400BadRequest();
				break;
			} catch (VersionNotSupportedException e) {
				responseFactory.writeResponse505HTTPVersionNotSupported();
				break;
			} catch (ServiceUnavailableException | SocketException e) {
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

	
	//Reads request, sends it back as String. 
	private String readRequest() throws BadRequestException, RequestTimeoutException {
		try {
			// Read from the socket.
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// string to save the content.
			StringBuilder content = new StringBuilder();

			// Reads until there is no more to read.
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					throw new BadRequestException();
				}
				content.append(line + "\r\n");
				if (line.equals("\r\n") || line.isEmpty() || line.equals("")) {
					break;
				}
			}
			// Returns everything read.
			return content.toString();
		} catch (IOException e) {
			throw new RequestTimeoutException();
		}
	}
}