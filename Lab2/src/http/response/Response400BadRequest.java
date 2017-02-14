package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response400BadRequest extends Response {

	private final String RESPONSE = "HTTP/1.1 400 Bad request\r\n";
	private final String CONTENT = "<html><body><h1>400 Bad request</h1></body></html>";
	private final String EXTENSION = "text/html";

	public Response400BadRequest(Socket socket) {
		super(socket);
	}
	
	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}