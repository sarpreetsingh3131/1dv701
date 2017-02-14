package http.response;

import java.io.File;

/*
 * the client press the refresh button too many times in a certain time period
 */

public class Response429TooManyRequests extends Response {

	private final String RESPONSE = "HTTP/1.1 429 Too Many Requests\r\n";
	private final String CONTENT = "<html><body><h1>429 Too Many Requests</h1></body></html>";
	private final String EXTENSION = "text/html";
	private final ResponseType RESPONSE_TYPE = ResponseType.Too_Many_Requests;

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