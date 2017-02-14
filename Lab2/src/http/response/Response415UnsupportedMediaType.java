package http.response;

import java.io.File;

/*
 * The server don't want to handle perhaps .jpg files but one is being uploaded to the server
 */

public class Response415UnsupportedMediaType extends Response {

	private final String RESPONSE = "HTTP/1.1 415 Unsupported Media Type\r\n";
	private final String CONTENT = "<html><body><h1>415 Unsupport Media Type</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.Unsupport_Media_Type;

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