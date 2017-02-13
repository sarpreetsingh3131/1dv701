package http;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import http.exceptions.UnknownRequestException;
import http.response.Response;
import http.response.Response.ResponseType;
import http.response.ResponseFactory;

public class ClientThread extends Thread {

	private final Socket socket;
	@SuppressWarnings("unused")
	private final int clientId;
	private ResponseFactory responseFactory;
	private byte[] buffer;
	private final int TIME_OUT = 10000;

	public ClientThread(Socket socket, int clientId, SharedFolder sharedFolder) {
		this.socket = socket;
		this.clientId = clientId;
		responseFactory = new ResponseFactory(sharedFolder);
		buffer = new byte[8192];
	}

	@Override
	public void run() {
		try {
			while (connected());
		} catch (IOException e) {
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean connected() throws IOException {
		boolean connection = true;
		socket.setSoTimeout(TIME_OUT);
		String userRequest = read(new BufferedReader(new InputStreamReader(socket.getInputStream())));
		Request request = new Request();
		Response response = null;

		try {
			response = responseFactory.getResponse(request.parseRequest(userRequest));
		} catch (UnknownRequestException | IOException e) {
			connection = false;
		}
		connection = !request.isConnectionClosed();
		replyToClient(response, connection);
		return connection;
	}

	private void replyToClient(Response response, boolean connection) throws IOException {
		PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
		printer.write(response.getResponseString(connection));
		printer.flush();

		if (response.getResponseType() == ResponseType.OK) {
			FileInputStream in = new FileInputStream(response.getFile());
			OutputStream out = socket.getOutputStream();

			int bytesRead = 0;

			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			in.close();
		} else {
			socket.getOutputStream().write(response.getContent().getBytes());
		}
	}

	public String read(BufferedReader reader) throws IOException {
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