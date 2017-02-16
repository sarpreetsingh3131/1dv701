package http;

import java.util.HashMap;
import java.util.Map;
import http.exceptions.BadRequestException;
import http.exceptions.VersionNotSupportedException;

public class Request {

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

	public Request parseRequest(String userRequest) throws BadRequestException, VersionNotSupportedException {
		String[] totalLines = userRequest.split("\r\n");
		
		if(totalLines.length == 0) {
			throw new BadRequestException();
		}
		
		String[] request = totalLines[0].split(" ");

		if (request.length != 3 || !request[2].split("/")[0].equals("HTTP")) {
			throw new BadRequestException();
		}
		
		if(!request[2].split("/")[1].equals(HTTP_VERSION)) {
			throw new VersionNotSupportedException();
		}
		
		Header header = new Header();
		Map<Header.Type, Header> headers = new HashMap<>();

		for (int i = 1; i < totalLines.length; i++) {
			Header h = header.getHeader(totalLines[i]);
			headers.put(h.getType(), h);
		}

		if (!headers.containsKey(Header.Type.Host)) {
			throw new BadRequestException();
		}
		return new Request(getType(request[0]), request[1]);
	}

	private Type getType(String type) throws BadRequestException {
		for (Type t : Type.values()) {
			if (type.equals(t.name())) {
				return t;
			}
		}
		throw new BadRequestException();
	}

	public Type getRequestType() {
		return type;
	}

	public String getPath() {
		return path;
	}
}