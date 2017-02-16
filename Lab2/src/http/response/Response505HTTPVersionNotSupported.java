package http.response;

import java.net.Socket;

public class Response505HTTPVersionNotSupported extends Response {

	public Response505HTTPVersionNotSupported(Socket socket) {
		super(socket, "505 HTTP Version Not Supported");
	}
}