package http;

import http.exceptions.UnknownRequestException;

public class Header {

	public enum Type {
		Host("Host"), 
		ContentType("Content-Type"), 
		ContentLength("Content-Length"), 
		Connection("Connection"), 
		CacheControl("Cache-Control"), 
		Accept("Accept"), 
		UserAgent("User-Agent"), 
		AcceptEncoding("Accept-Encoding"), 
		AcceptLanguage("Accept-Language"), 
		UnknownHeader("Unknown-Header");

		String type;
		
		private Type(String type) {
			this.type = type;
		}
	}

	private Type type;
	private String typeValue;

	public Header(String typeValue, Type type) {
		this.typeValue = typeValue;
		this.type = type;
	}

	public static Header getHeader(String header) throws UnknownRequestException {
		if (header.split(": ").length != 2) {
			throw new UnknownRequestException("(Incorrect header: " + header + ")");
		}
		for (Type type : Type.values()) {
			if (header.startsWith(type.type)) {
				return new Header(header.split(":")[1], type);
			}
		}
		return new Header(header.split(":")[1], Type.UnknownHeader);
	}

	public Type getType() {
		return type;
	}

	public String getTypeValue() {
		return typeValue;
	}
}