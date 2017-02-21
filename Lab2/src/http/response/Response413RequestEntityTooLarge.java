package http.response;

import http.ServerThread;

public class Response413RequestEntityTooLarge extends Response {

	public Response413RequestEntityTooLarge(ServerThread client) {
		super(client, "413 Request Entity Too Large");
	}
}
