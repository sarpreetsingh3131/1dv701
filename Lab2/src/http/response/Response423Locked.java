package http.response;

import http.ServerThread;

/*
 * This class forms the specific 423 response.
 */
public class Response423Locked extends Response {

	public Response423Locked(ServerThread client) {
		super(client, "423 Locked");
	}
}
