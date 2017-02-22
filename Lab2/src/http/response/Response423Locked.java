package http.response;

import http.ServerThread;

public class Response423Locked extends Response {

	public Response423Locked(ServerThread client) {
		super(client, "423 Locked", "The resource that is being accessed is locked.");
	}
}
