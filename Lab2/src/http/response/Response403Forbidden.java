package http.response;

import http.ServerThread;

/*
 * This class forms the specific 403 response.
 */
public class Response403Forbidden extends Response {

	public Response403Forbidden(ServerThread client) {
		super(client, "403 Forbidden",
				"The request is valid but does not have the necessary permissions for accessing the resource.");
	}
}