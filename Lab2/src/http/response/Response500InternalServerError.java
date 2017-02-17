package http.response;

import java.net.Socket;

/*
 * This class forms the specific 500 response.
 */
public class Response500InternalServerError extends Response {

	public Response500InternalServerError(Socket socket) {
		super(socket, "500 Internal Server Error");
	}

}