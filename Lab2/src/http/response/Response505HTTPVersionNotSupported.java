package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response505HTTPVersionNotSupported extends Response {

	private final String RESPONSE = "HTTP/1.1 505 HTTP Version not supported\r\n";
	private final String CONTENT = "<html><body><h1>505 HTTP Version not supported</h1></body></html>";
	private final String EXTENSION = "html";

	public Response505HTTPVersionNotSupported(Socket socket) {
		super(socket);
	}

	@Override
	public void write() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}