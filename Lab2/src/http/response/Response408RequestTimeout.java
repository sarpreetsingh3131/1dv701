package http.response;

import java.net.Socket;

public class Response408RequestTimeout extends Response {

	public Response408RequestTimeout(Socket socket) {
		super(socket, "408 Request Time-out");
	}
}
