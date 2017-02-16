package http.response;

import java.net.Socket;

public class Response408Timeout extends Response {

	public Response408Timeout(Socket socket) {
		super(socket, "408 Time-out","408 Time-out","html");
	}

}