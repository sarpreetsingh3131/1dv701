package http.response;

import java.net.Socket;

public class Response400BadRequest extends Response {

	public Response400BadRequest(Socket socket) {
		super(socket, "400 bad request", "400 Bad request","html" );
	}
}