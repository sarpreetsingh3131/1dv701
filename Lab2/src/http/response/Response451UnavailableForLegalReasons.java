package http.response;

import java.net.Socket;

public class Response451UnavailableForLegalReasons extends Response {

	public Response451UnavailableForLegalReasons(Socket socket) {
		super(socket, "451 Unavailabe For Legal Reasons");
	}
}
