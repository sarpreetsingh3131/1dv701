package http.response;

import java.io.File;

/*
 * file could not be processed because the file is currently being edited/modified 
 */

public class Response409Conflict extends Response {

	private final String RESPONSE = "HTTP/1.1 409 Conflict\r\n";
	private final String CONTENT = "<html><body><h1>409 Conflict</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.Conflict;

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