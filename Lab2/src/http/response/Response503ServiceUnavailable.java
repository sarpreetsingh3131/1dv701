package http.response;

import java.net.Socket;

public class Response503ServiceUnavailable extends Response {

	public Response503ServiceUnavailable(Socket socket) {
		super(socket, "503 Service Unavailable");
	}

}