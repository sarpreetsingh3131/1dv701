package http.response;

import java.net.Socket;
import http.Request;
import http.exceptions.InternalServerException;

public class Response405MethodNotSupported extends Response {

	private final String RESPONSE;
	private final String CONTENT = "<html><body><h1>405 Method not supported</h1></body></html>";
	private final String EXTENSION = "text/html";
	
	public Response405MethodNotSupported(Request.Type type, Socket socket) {
		super(socket);
		System.out.println(type.toString());
		RESPONSE = "HTTP/1.1 405 Method " + type.toString() + " Not Allowed\r\n";
	}

	@Override
	public void sendResponse() throws InternalServerException {
		super.writeHeader(RESPONSE, CONTENT.getBytes().length, EXTENSION);
		super.writeContent(CONTENT);
	}
}