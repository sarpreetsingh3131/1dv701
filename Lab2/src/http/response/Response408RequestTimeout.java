package http.response;

import java.net.Socket;

/*
 * This class forms the specific 408 response.
 */
public class Response408RequestTimeout extends Response {

	public Response408RequestTimeout(Socket socket) {
		super(socket, "408 Request Time-out");
	}
}
