package http;

import java.util.HashMap;
import java.util.Map;
import http.exceptions.UnknownRequestException;

public class Request {

	public enum Type {GET}
	private Type type;
	private String URL;
	private Map<Header.Type, Header> headers;

	public Request() {
		URL = "";
		headers = new HashMap<Header.Type, Header>();
	}

	public Request parseRequest(String userRequest) throws UnknownRequestException {
		String[] totalLines = userRequest.split("\r\n");
		String[] request = totalLines[0].split(" ");
	
		if (request.length != 3) {
			throw new UnknownRequestException("(Incorrect request: " + userRequest + ")");
		}

		this.type = getType(request[0]);
		this.URL = request[1];
		
		for (int i = 1; i < totalLines.length; i++) {
			Header header = Header.getHeader(totalLines[i]);
			headers.put(header.getType(), header);
		}
	
		if (!headers.containsKey(Header.Type.Host)) {
			throw new UnknownRequestException("Header is missing!!");
		}

		return this;
	}

	public boolean isConnectionClosed() {
		try {
			return headers.get(Header.Type.Connection).getTypeValue().contains("close");
		} catch (Exception e) {
			return false;
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

	public String getURL() {
		return URL;
	}
}