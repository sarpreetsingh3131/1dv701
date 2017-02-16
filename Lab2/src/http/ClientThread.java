package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import http.exceptions.InternalServerException;
import http.exceptions.VersionNotSupportedException;
import http.exceptions.BadRequestException;
import http.response.ResponseFactory;

public class ClientThread extends Thread {

	private final Socket socket;
	private ResponseFactory responseFactory;
	private Request request;
	private byte[] buffer;

	public ClientThread(Socket socket) {
		this.socket = socket;
		request = new Request();
		buffer = new byte[8192];
		responseFactory = new ResponseFactory(new SharedFolder(), this.socket, this.buffer);
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket.setSoTimeout(9000);
				responseFactory.getResponse(request.parseRequest(readRequest())).write();
			} catch (SocketException | InternalServerException e) {
				responseFactory.writeResponse500InternalServerError();
				break;
			} catch (BadRequestException e) {
				responseFactory.writeResponse400BadRequest();
				break;
			} catch (VersionNotSupportedException e) {
				responseFactory.writeResponse505HTTPVersionNotSupported();
				break;
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			responseFactory.writeResponse500InternalServerError();
		}
	}

	private String readRequest() throws BadRequestException, InternalServerException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder content = new StringBuilder();
			if(content.toString().isEmpty()) throw new InternalServerException();
			
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
			return content.toString();
		} catch (IOException e) {
			throw new InternalServerException();
		}
	}
}