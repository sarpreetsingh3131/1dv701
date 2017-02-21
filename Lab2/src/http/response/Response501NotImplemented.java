package http.response;

import http.ServerThread;

/*
 * This class forms the specific 501 response.
 */
public class Response501NotImplemented extends Response {

	public Response501NotImplemented(ServerThread client) {
		super(client, "501 Not Implemented");
	}
}