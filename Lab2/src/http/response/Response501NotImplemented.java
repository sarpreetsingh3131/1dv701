package http.response;

import http.ClientThread;

/*
 * This class forms the specific 501 response.
 */
public class Response501NotImplemented extends Response {

	public Response501NotImplemented(ClientThread client) {
		super(client, "501 Not Implemented");
	}
}