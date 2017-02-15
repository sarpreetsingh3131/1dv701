package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response415UnsupportedMediaType extends Response {

	private final String RESPONSE = "HTTP/1.1 415 Unsupported Media Type\r\n";
	private final String CONTENT = "<html><body><h1>415 Unsupport Media Type</h1></body></html>";
	private final String EXTENSION = "html";
	
	public Response415UnsupportedMediaType(Socket socket) {
		super(socket);
	}
	
	@Override
	public void write() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}