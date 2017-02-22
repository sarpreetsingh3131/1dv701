package http.response;

import http.ServerThread;

public class Response408RequestTimeout extends Response {

	public Response408RequestTimeout(ServerThread client) {
		super(client, "408 Request Time-out", "The server timed out waiting for the request.");
	}
}
