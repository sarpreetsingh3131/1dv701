package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import http.exceptions.InternalServerException;
import http.exceptions.UnknownRequestException;
import http.response.Response;
import http.response.ResponseFactory;

public class ClientThread extends Thread {

	private final Socket socket;
	@SuppressWarnings("unused")
	private final int clientId;
	private ResponseFactory responseFactory;
	private Request request;
	private byte[] buffer;
	private final int TIME_OUT = 10000;

	public ClientThread(Socket socket, int clientId) {
		this.socket = socket;
		this.clientId = clientId;
		request = new Request();
		buffer = new byte[8192];
		responseFactory = new ResponseFactory(new SharedFolder(), this.socket, this.buffer);
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket.setSoTimeout(TIME_OUT);
				String userRequest = readRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				Response response = responseFactory.getResponse(request.parseRequest(userRequest));
				response.sendResponse();
			} catch (InternalServerException | IOException e) {
				responseFactory.writeResponse500InternalServerError();
				break;
			} catch (UnknownRequestException e) {
				responseFactory.writeResponse400BadRequest();
				break;
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			responseFactory.writeResponse500InternalServerError();
		}
	}

	private String readRequest(BufferedReader reader) throws IOException {
		StringBuilder content = new StringBuilder();

		while (true) {
			String line = reader.readLine();

			if (line == null) {
				throw new IOException();
			}
			content.append(line + "\r\n");

			if (line.equals("\r\n") || line.isEmpty() || line.equals("")) {
				break;
			}
		}
		return content.toString();
	}
}