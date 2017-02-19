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
	public enum Method {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	private Method method;
	private String path;
	private final String HTTP_VERSION = "HTTP/1.1";
	private boolean connectionClosed = false;
	private int contentLength = 0;
	private StringBuilder body = new StringBuilder();

	private Request(Method type, String path) {
		this.method = type;
		this.path = path;
	}

	public Request() {
	}

	public Request parseRequest(Socket socket, int timeout)
			throws BadRequestException, VersionNotSupportedException, RequestTimeoutException, InternalServerException {
		
		String header = read(socket, timeout);

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
		
		//Host: localhost:8080
		if(!totalLines[1].split(": ")[1].equals("localhost:8080")) {
			throw new BadRequestException();
		}
		
		for(int i = 1; i < totalLines.length; i++) {
			if(totalLines[i].startsWith("Connection")) {
				connectionClosed = totalLines[i].split(": ")[1].equals("close");
				break;
			}
		}

		return new Request(findMetod(request[0]), request[1]);
	}

	// Reads request, sends it back as String.
	private String read(Socket socket, int timeout) throws RequestTimeoutException, InternalServerException, BadRequestException {
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
				
				if(line.startsWith("Content-Length")) {
					try{ //Content-Length.length() = 16
						contentLength = Integer.parseInt(line.substring(16));
					}catch(Exception e) {
						throw new BadRequestException();
					}
				}
			}
			//Read content
			for(int i = 0; i < contentLength; i++) {
				body.append((char)reader.read());
			}
			
			return header.toString();
		} catch (IOException e) {
			if (System.currentTimeMillis() - time > timeout) {
				throw new RequestTimeoutException();
			}
			throw new InternalServerException();
		}
	}

	// Gets the type that is to be set.
	private Method findMetod(String method) throws BadRequestException {
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
}