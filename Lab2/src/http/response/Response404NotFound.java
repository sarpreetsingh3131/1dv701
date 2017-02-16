package http.response;

import java.net.Socket;

public class Response404NotFound extends Response {

	public Response404NotFound(Socket socket) {
		super(socket, "404 Not Found");
	}

}