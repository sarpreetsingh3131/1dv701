package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response500InternalServerError extends Response {

	private final String RESPONSE = "HTTP/1.1 500 Internal Server Error\r\n";
	private final String CONTENT = "<html><body><h1>500 Internal Server Error</h1></body></html>";
	private final String EXTENSION = "text/html";

	public Response500InternalServerError(Socket socket) {
		super(socket);
	}

	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}