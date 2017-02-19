package http.response;

import http.ClientThread;

/*
 * This class forms the specific 423 response.
 */
public class Response423Locked extends Response {

	public Response423Locked(ClientThread client) {
		super(client, "423 Locked");
	}
}
