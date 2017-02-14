package http;

import java.util.HashMap;
import java.util.Map;
import http.exceptions.UnknownRequestException;

public class Request {

	public enum Type {GET}
	private Type type;
	private String path;

	private Request(Type type, String path) {
		this.type = type;
		this.path = path;
	}

	public Request() {
	}

	public Request parseRequest(String userRequest) throws UnknownRequestException {
		String[] totalLines = userRequest.split("\r\n");
		String[] request = totalLines[0].split(" ");

		if (request.length != 3) {
			throw new UnknownRequestException("(Incorrect request: " + userRequest + ")");
		}
		Header header = new Header();
		Map<Header.Type, Header> headers = new HashMap<>();

		for (int i = 1; i < totalLines.length; i++) {
			Header h = header.getHeader(totalLines[i]);
			headers.put(h.getType(), h);
		}

		if (!headers.containsKey(Header.Type.Host)) {
			throw new UnknownRequestException("Header is missing!!");
		}

		return new Request(getType(request[0]), request[1]);
	}

	private Type getType(String type) throws UnknownRequestException {
		for (Type m : Type.values()) {
			if (type.equals(m.name())) {
				return m;
			}
		}
		throw new UnknownRequestException("(Unknown Request/method type: " + type + ")");
	}

	public Type getRequestType() {
		return type;
	}

	public String getPath() {
		return path;
	}
}