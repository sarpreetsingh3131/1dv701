package http.response;

import http.ServerThread;

/*
 * This class forms the specific 505 response.
 */
public class Response505HTTPVersionNotSupported extends Response {

	public Response505HTTPVersionNotSupported(ServerThread client) {
		super(client, "505 HTTP Version Not Supported");
	}
}