package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import http.exceptions.BadRequestException;
import http.exceptions.InternalServerException;
import http.exceptions.RequestTimeoutException;
import http.exceptions.VersionNotSupportedException;

/*
 * A class containing data about for request.
 */
public class Request {

	// Request types.
	public enum Type {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	private Type type;
	private String path;
	private final String HTTP_VERSION = "HTTP/1.1";

	private Request(Type type, String path) {
		this.type = type;
		this.path = path;
	}

	public Request() {
	}

	public Request parseRequest(Socket socket, int timeout)
			throws BadRequestException, VersionNotSupportedException, RequestTimeoutException, InternalServerException {
		
		String userRequest = readRequest(socket, timeout);

		// splits header types.
		String[] totalLines = userRequest.split("\r\n");
		
		// splits first line.
		String[] request = totalLines[0].split(" ");

		// Checks if length is 3 (GET / HTTP/1.1), if not then throw 400.
		if (request.length != 3) {
			throw new BadRequestException();
		}
		
		// Checks the right HTTP version or throw 505.
		if (!request[2].equals(HTTP_VERSION)) {
			throw new VersionNotSupportedException();
		}

		return new Request(getType(request[0]), request[1]);
	}

	// Reads request, sends it back as String.
	private String readRequest(Socket socket, int timeout) throws RequestTimeoutException, InternalServerException {
		long time = 0;
		try {
			time = System.currentTimeMillis();
			socket.setSoTimeout(timeout);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder content = new StringBuilder();

			// Reads until there is no more to read.
			while (true) {
				String line = reader.readLine();
				if (line == null || line.equals("\r\n") || line.isEmpty() || line.equals("")) {
					break;
				}
				content.append(line + "\r\n");
			}
			
			return content.toString();
		} catch (IOException e) {
			if (System.currentTimeMillis() - time > timeout) {
				throw new RequestTimeoutException();
			}
			throw new InternalServerException();
		}
	}

	// Gets the type that is to be set.
	private Type getType(String type) throws BadRequestException {
		for (Type t : Type.values()) {
			if (type.equals(t.name())) {
				return t;
			}
		}
		// If the type does not exist then throw 400.
		throw new BadRequestException();
	}

	public Type getRequestType() {
		return type;
	}

	public String getPath() {
		return path;
	}
}