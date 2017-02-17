package http;

import java.util.HashMap;
import java.util.Map;
import http.exceptions.BadRequestException;
import http.exceptions.VersionNotSupportedException;

/*
 * A class containing data about for request.
 */
public class Request {

	//Request types.
	public enum Type {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	
	private Type type;
	private String path;
	private final String HTTP_VERSION = "1.1";

	private Request(Type type, String path) {
		this.type = type;
		this.path = path;
	}

	public Request() {
	}

	
	 // Takes the request as string and reads its contents and creates a new request.
	 // Returns exceptions if the request is bad.
	 
	public Request parseRequest(String userRequest) throws BadRequestException, VersionNotSupportedException {
		//splits header types.
		String[] totalLines = userRequest.split("\r\n");

		// If content length is 0 then throw 400.
		if (totalLines.length == 0) {
			throw new BadRequestException();
		}
		
		// splits first line.
		String[] request = totalLines[0].split(" ");
		// Checks if it contains HTTP and length of 3, if not then throw 400.
		if (request.length != 3 || !request[2].split("/")[0].equals("HTTP")) {
			throw new BadRequestException();
		}
		// Checks the right HTTP version or throw 505.
		if(!request[2].split("/")[1].equals(HTTP_VERSION)) {
			throw new VersionNotSupportedException();
		}
		
		// A map to store headers.
		Header header = new Header();
		Map<Header.Type, Header> headers = new HashMap<>();
		
		// Fill the map with headers.
		for (int i = 1; i < totalLines.length; i++) {
			Header h = header.getHeader(totalLines[i]);
			headers.put(h.getType(), h);
		}
		// Checks if the header contains type HOST, if not then throw 400.
		if (!headers.containsKey(Header.Type.Host)) {
			throw new BadRequestException();
		}
		
		// Return a new request with type and path.
		return new Request(getType(request[0]), request[1]);
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