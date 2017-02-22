package http.response;

import http.ServerThread;

public class Response500InternalServerError extends Response {

	public Response500InternalServerError(ServerThread client) {
		super(client, "500 Internal Server Error", "Server have some internal problem");
	}
}