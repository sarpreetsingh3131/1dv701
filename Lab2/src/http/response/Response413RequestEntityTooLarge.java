package http.response;

import http.ClientThread;

public class Response413RequestEntityTooLarge extends Response {

	public Response413RequestEntityTooLarge(ClientThread client) {
		super(client, "413 Request Entity Too Large");
	}
}
