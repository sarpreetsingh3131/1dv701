package http.response;

import java.net.Socket;

/*
 * This class forms the specific 403 response.
 */
public class Response403Forbidden extends Response {

	public Response403Forbidden(Socket socket) {
		super(socket, "403 Forbidden");
	}
}