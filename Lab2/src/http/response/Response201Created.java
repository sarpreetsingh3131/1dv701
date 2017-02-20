package http.response;

import http.ClientThread;

public class Response201Created extends Response {

	public Response201Created(ClientThread client) {
		super(client, "201 Created");
	}
}