package http.response;

import http.ServerThread;

public class Response409Conflict extends Response {

	public Response409Conflict(ServerThread client, String filename) {
		super(client, "409 Conflict",
				"The request could notbe processed because " + filename + " does not exist. Please use POST request.");
	}
}