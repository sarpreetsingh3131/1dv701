package http.response;

import http.ClientThread;

/*
 * This class forms the specific 400 response.
 */
public class Response400BadRequest extends Response {

	public Response400BadRequest(ClientThread client) {
		super(client, "400 Bad Request" );
	}
}