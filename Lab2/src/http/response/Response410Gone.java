package http.response;

import java.net.Socket;

public class Response410Gone extends Response {
	
	public Response410Gone(Socket socket) {
		super(socket, "410 Gone", "410 Gone", "html");
	}
	
}