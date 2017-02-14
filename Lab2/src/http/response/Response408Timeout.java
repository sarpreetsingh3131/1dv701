package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

/*
 * when the client times out when trying to connect to the server.
 */

public class Response408Timeout extends Response {

	private final String RESPONSE = "HTTP/1.1 408 Time-out\r\n";
	private final String CONTENT = "<html><body><h1>408 Time-out</h1></body></html>";
	private final String EXTENSION = "text/html";

	public Response408Timeout(Socket socket) {
		super(socket);
	}

	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}