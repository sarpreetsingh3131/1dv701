package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response409Conflict extends Response {

	private final String RESPONSE = "HTTP/1.1 409 Conflict\r\n";
	private final String CONTENT = "<html><body><h1>409 Conflict</h1></body></html>";
	private final String EXTENSION = "html";
	
	public Response409Conflict(Socket socket) {
		super(socket);
	}
	
	@Override
	public void write() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}