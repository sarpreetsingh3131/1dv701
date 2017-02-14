package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

/*
 * the client press the refresh button too many times in a certain time period
 */

public class Response429TooManyRequests extends Response {

	private final String RESPONSE = "HTTP/1.1 429 Too Many Requests\r\n";
	private final String CONTENT = "<html><body><h1>429 Too Many Requests</h1></body></html>";
	private final String EXTENSION = "text/html";
	
	public Response429TooManyRequests(Socket socket) {
		super(socket);
	}
	
	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}