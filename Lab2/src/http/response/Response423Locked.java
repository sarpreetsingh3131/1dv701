package http.response;

import java.net.Socket;

/*
 * This class forms the specific 423 response.
 */
public class Response423Locked extends Response {

	public Response423Locked(Socket socket) {
		super(socket, "423 Locked");
	}
}
