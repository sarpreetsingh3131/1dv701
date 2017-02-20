package http.response;

import http.ClientThread;

public class Response415UnsupportedMediaType extends Response {

	public Response415UnsupportedMediaType(ClientThread client) {
		super(client, "415 Unsupported Media Type");
	}
}
