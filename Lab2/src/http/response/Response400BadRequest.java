package http.response;

import http.ServerThread;

/*
 * This class forms the specific 400 response.
 */
public class Response400BadRequest extends Response {

	public Response400BadRequest(ServerThread client) {
		super(client, "400 Bad Request" );
	}
}