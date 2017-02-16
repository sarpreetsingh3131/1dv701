package http.response;

import java.net.Socket;

public class Response423Locked extends Response {

	public Response423Locked(Socket socket) {
		super(socket, "423 Locked");
	}
}
