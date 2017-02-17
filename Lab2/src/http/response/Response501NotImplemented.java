package http.response;

import java.net.Socket;

/*
 * This class forms the specific 501 response.
 */
public class Response501NotImplemented extends Response {

	public Response501NotImplemented(Socket socket) {
		super(socket,"501 Not Implemented");
	}

}