package http.response;

import java.io.File;

/*
 * when the client times out when trying to connect to the server.
 */

public class Response408Timeout extends Response {

	private final String RESPONSE = "HTTP/1.1 408 Time-out\r\n";
	private final String CONTENT = "<html><body><h1>408 Time-out</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.Time_out;

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