package http;

import http.exceptions.BadRequestException;

/*
 * This class contains one header type and value.
 */
public class Header {

	public enum Type {
		/*
		 * content types.
		 */
		Host("Host"), ContentType("Content-Type"), ContentLength("Content-Length"), Connection(
				"Connection"), CacheControl("Cache-Control"), Accept("Accept"), UserAgent("User-Agent"), AcceptEncoding(
						"Accept-Encoding"), AcceptLanguage("Accept-Language"), UnknownHeader("Unknown-Header");

		String type;

		private Type(String type) {
			this.type = type;
		}
	}

	private Type type;
	private String typeValue;

	public Header() {
	}

	// Creates header with type and value.
	private Header(String typeValue, Type type) {
		this.type = type;
		this.typeValue = typeValue;
	}

	// Returns a new Header.
	public Header getHeader(String header) throws BadRequestException {
		// checks if there is a type and value.
		if (header.split(": ").length != 2) {
			throw new BadRequestException();
		}
		// Returns if it can find the type.
		for (Type type : Type.values()) {
			if (header.startsWith(type.type)) {
				return new Header(header.split(":")[1], type);
			}
		}
		// else return a unknownHeader type.
		return new Header(header.split(":")[1], Type.UnknownHeader);
	}

	public Type getType() {
		return type;
	}

	public String getTypeValue() {
		return typeValue;
	}
}