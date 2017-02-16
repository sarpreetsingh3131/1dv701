package http.response;

import java.net.Socket;

public class Response500InternalServerError extends Response {

	public Response500InternalServerError(Socket socket) {
		super(socket, "500 Internal Server Error");
	}

}