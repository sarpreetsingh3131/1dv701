package http.response;

import http.ClientThread;

/*
 * This class forms the specific 500 response.
 */
public class Response500InternalServerError extends Response {

	public Response500InternalServerError(ClientThread client) {
		super(client, "500 Internal Server Error");
	}
}