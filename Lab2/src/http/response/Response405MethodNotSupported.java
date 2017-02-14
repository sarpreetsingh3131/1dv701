package http.response;

import java.io.File;
import http.Request;

public class Response405MethodNotSupported extends Response {

	private final String RESPONSE;
	private final String CONTENT = "<html><body><h1>400 Bad request</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.METHOD_NOT_SUPPORTED;

	public Response405MethodNotSupported(Request.Type type) {
		RESPONSE = "HTTP/1.1 405 Method " + type.toString() + " not supported\r\n";
	}

	@Override
	public String getResponseString(boolean connection) {
		return RESPONSE + super.getContentLengthAndType(CONTENT.getBytes().length, EXTENSION)
				+ super.getConnection(connection);
	}

	@Override
	public ResponseType getResponseType() {
		return RESPONSE_TYPE;
	}

	@Override
	public String getContent() {
		return CONTENT;
	}

	@Override
	public File getFile() {
		return null;
	}
}