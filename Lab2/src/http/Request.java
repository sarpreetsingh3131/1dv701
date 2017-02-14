package http;

import java.util.HashMap;
import java.util.Map;
import http.exceptions.UnknownRequestException;

public class Request {

	public enum Type {GET}
	private Type type;
	private String path;
	private Map<Header.Type, Header> headers;
	private Header header;

	public Request() {
		header = new Header();
	}

	private Request(Type type, String path, Map<http.Header.Type, Header> headers) {
		this.type = type;
		this.path = path;
		this.headers = headers;
		header = new Header();
	}

	public Request parseRequest(String userRequest) throws UnknownRequestException {
		String[] totalLines = userRequest.split("\r\n");
		String[] request = totalLines[0].split(" ");

		if (request.length != 3) {
			throw new UnknownRequestException("(Incorrect request: " + userRequest + ")");
		}

		Map<Header.Type, Header> headers = new HashMap<>();

		for (int i = 1; i < totalLines.length; i++) {
			Header header = this.header.getHeader(totalLines[i]);
			headers.put(header.getType(), header);
		}

		if (!headers.containsKey(Header.Type.Host)) {
			throw new UnknownRequestException("Header is missing!!");
		}

		return new Request(getType(request[0]), request[1], headers);
	}

	public boolean needMoreConnection() {
		try {
			return !headers.get(Header.Type.Connection).getTypeValue().contains("close");
		} catch (Exception e) {
			return true;
		}
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