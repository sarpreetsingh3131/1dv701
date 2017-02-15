package http.response;

import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response410Gone extends Response {

	private final String RESPONSE = "HTTP/1.1 410 Gone\r\n";
	private final String CONTENT = "<html><body><h1>410 Gone</h1></body></html>";
	private final String EXTENSION = "html";
	
	public Response410Gone(Socket socket) {
		super(socket);
	}
	
	@Override
	public void write() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}