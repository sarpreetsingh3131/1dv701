package http.response;

import java.io.File;

public class Response403Forbidden extends Response {

	private final String RESPONSE = "HTTP/1.1 403 Forbidden\r\n";
	private final String CONTENT = "<html><body><h1>403 Forbidden</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.FORBIDDEN;

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