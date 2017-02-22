package http.response;

import http.ServerThread;

public class Response201Created extends Response {

	public Response201Created(ServerThread client, String filename) {
		super(client, "201 Created", "The request has been fulfilled, resulting in the creation of " + filename);
	}
}