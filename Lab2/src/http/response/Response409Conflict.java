package http.response;

import java.net.Socket;

public class Response409Conflict extends Response {
	
	public Response409Conflict(Socket socket) {
		super(socket,"409 Conflict");
	}

}