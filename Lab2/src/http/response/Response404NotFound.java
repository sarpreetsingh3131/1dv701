package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response404NotFound extends Response {

	private final String RESPONSE = "HTTP/1.1 404 Not Found\r\n";
	private final String CONTENT = "<html><body><h1>404 Not Found</h1></body></html>";
	private final String EXTENSION = "text/html";

	public Response404NotFound(Socket socket) {
		super(socket);
	}

	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}