package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import http.exceptions.BadRequestException;
import http.exceptions.InternalServerException;
import http.exceptions.RequestEntityTooLargeException;
import http.exceptions.RequestTimeoutException;
import http.exceptions.VersionNotSupportedException;

/*
 * A class containing data about for request.
 */
public class Request {

	// Request types.
	public enum Method {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	private Method method;
	private String path;
	private final String HTTP_VERSION = "HTTP/1.1";
	private boolean connectionClosed = false;
	private final int MAX_CONTENT_LENGTH_IN_KB = 256; //256kb max
	private long contentLength = 0;
	private StringBuilder body;

	private Request(Method type, String path, StringBuilder body, boolean connectionClosed) {
		this.method = type;
		this.path = path;
		this.body = body;
		this.connectionClosed = connectionClosed;
	}

	public Request() {
	}

	public Request parseRequest(Socket socket, int timeout) throws BadRequestException, VersionNotSupportedException,
			RequestTimeoutException, InternalServerException, IOException, RequestEntityTooLargeException {

		String header = readHeader(socket, timeout);

		// splits header types.
		String[] totalLines = header.split("\r\n");

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

		// Host: localhost:8080
		if (!totalLines[1].split(": ")[0].equals("Host")) {
			throw new BadRequestException();
		}

		// Connection: close || Connection: keep-alive
		for (int i = 1; i < totalLines.length; i++) {
			if (totalLines[i].startsWith("Connection")) {
				connectionClosed = totalLines[i].split(": ")[1].equals("close");
				break;
			}
		}
		return new Request(findMethod(request[0]), request[1], body, connectionClosed);
	}

	// Reads request, sends it back as String.
	private String readHeader(Socket socket, int timeout) throws RequestTimeoutException, InternalServerException,
			BadRequestException, RequestEntityTooLargeException {
		long time = 0;
		try {
			time = System.currentTimeMillis();
			socket.setSoTimeout(timeout);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder header = new StringBuilder();

			// Reads until there is no more to read.
			while (true) {
				String line = reader.readLine();

				if (line == null || line.equals("\r\n") || line.isEmpty() || line.equals("")) {
					break;
				}
				header.append(line + "\r\n");

				if (line.startsWith("Content-Length")) {
					try { // "Content-Length: ".length() = 16
						contentLength = Integer.parseInt(line.substring(16));
					} catch (Exception e) {
						throw new BadRequestException();
					}
				}
			}
			readBody(reader);
			return header.toString();
		} catch (IOException e) {
			if (System.currentTimeMillis() - time > timeout) {
				throw new RequestTimeoutException();
			}
			throw new InternalServerException();
		}
	}

	private void readBody(BufferedReader reader) throws IOException, RequestEntityTooLargeException {
		// base64 is 100kb more than actual, 8bits is 1 char
		long contentKb = ((contentLength * 8) / 10000);
		
		if (contentKb > MAX_CONTENT_LENGTH_IN_KB) {
			throw new RequestEntityTooLargeException();
		}
		
		body = new StringBuilder();
		for (int i = 0; i < contentLength; i++) {
			char ch = (char) reader.read();
			if (ch == '%') {
				// change from hexa to char
				ch = (char) Integer.parseInt("" + (char) reader.read() + "" + (char) reader.read(), 16);
				i += 2;
			}
			body.append(ch);
		}
	}

	// Gets the type that is to be set.
	private Method findMethod(String method) throws BadRequestException {
		for (Method m : Method.values()) {
			if (method.equals(m.name())) {
				return m;
			}
		}
		// If the type does not exist then throw 400.
		throw new BadRequestException();
	}

	public Method getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public boolean connectionClosed() {
		return connectionClosed;
	}

	public String getBody() {
		return body.toString();
	}
}