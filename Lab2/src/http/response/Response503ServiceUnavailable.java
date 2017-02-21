package http.response;

import http.ServerThread;

/*
 * This class forms the specific 503 response.
 */
public class Response503ServiceUnavailable extends Response {

	public Response503ServiceUnavailable(ServerThread client) {
		super(client, "503 Service Unavailable");
	}
}