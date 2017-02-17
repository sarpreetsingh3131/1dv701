package http.response;

import java.net.Socket;

/*
 * This class forms the specific 400 response.
 */
public class Response400BadRequest extends Response {

	public Response400BadRequest(Socket socket) {
		super(socket, "400 Bad Request" );
	}
}