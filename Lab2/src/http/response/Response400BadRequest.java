package http.response;

import http.ServerThread;

public class Response400BadRequest extends Response {

	public Response400BadRequest(ServerThread client) {
		super(client, "400 Bad Request", "The server cannot process the request due to an apparent client error.");
	}
}