package http.response;

import java.net.Socket;

public class Response415UnsupportedMediaType extends Response {
	
	public Response415UnsupportedMediaType(Socket socket) {
		super(socket, "415 Unsupported Media Type");
	}
	
}