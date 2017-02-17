package http.response;

import java.net.Socket;

/*
 * This class forms the specific 404 response.
 */
public class Response404NotFound extends Response {

	public Response404NotFound(Socket socket) {
		super(socket, "404 Not Found");
	}

}