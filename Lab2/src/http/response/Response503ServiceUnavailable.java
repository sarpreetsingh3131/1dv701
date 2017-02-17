package http.response;

import java.net.Socket;

/*
 * This class forms the specific 503 response.
 */
public class Response503ServiceUnavailable extends Response {

	public Response503ServiceUnavailable(Socket socket) {
		super(socket, "503 Service Unavailable");
	}

}