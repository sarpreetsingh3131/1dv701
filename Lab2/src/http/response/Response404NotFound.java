package http.response;

import http.ServerThread;

/*
 * This class forms the specific 404 response.
 */
public class Response404NotFound extends Response {

	public Response404NotFound(ServerThread client) {
		super(client, "404 Not Found");
	}
}