package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response501NotImplemented extends Response {

	private final String RESPONSE = "HTTP/1.1 501 Not Implemented\r\n";
	private final String CONTENT = "<html><body><h1>501 Requested method is not implemenetd</h1></body></html>";
	private final String EXTENSION = "html";

	public Response501NotImplemented(Socket socket) {
		super(socket);
	}

	@Override
	public void write() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}