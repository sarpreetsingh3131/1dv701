package http.response;

import java.net.Socket;

/*
 * This class forms the specific 505 response.
 */
public class Response505HTTPVersionNotSupported extends Response {

	public Response505HTTPVersionNotSupported(Socket socket) {
		super(socket, "505 HTTP Version Not Supported");
	}
}