package http.response;

import http.ServerThread;

/*
 * This class forms the specific 500 response.
 */
public class Response500InternalServerError extends Response {

	public Response500InternalServerError(ServerThread client) {
		super(client, "500 Internal Server Error");
	}
}