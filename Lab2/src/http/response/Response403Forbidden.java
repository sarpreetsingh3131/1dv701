package http.response;

import java.net.Socket;

public class Response403Forbidden extends Response {

	public Response403Forbidden(Socket socket) {
		super(socket, "403 Forbidden", "403 Forbidden", "html");
	}
}