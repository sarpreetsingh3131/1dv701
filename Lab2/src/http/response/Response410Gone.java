package http.response;

import java.io.File;

/*
 * the requested file is no longer available. Deleted or something
 */

public class Response410Gone extends Response {

	private final String RESPONSE = "HTTP/1.1 410 Gone\r\n";
	private final String CONTENT = "<html><body><h1>410 Gone</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.Gone;

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