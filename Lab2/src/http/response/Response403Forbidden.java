package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response403Forbidden extends Response {

	private final String RESPONSE = "HTTP/1.1 403 Forbidden\r\n";
	private final String CONTENT = "<html><body><h1>403 Forbidden</h1></body></html>";
	private final String EXTENSION = "text/html";

	public Response403Forbidden(Socket socket) {
		super(socket);
	}

	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}