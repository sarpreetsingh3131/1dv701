package http.response;

import http.ClientThread;

/*
 * This class forms the specific 505 response.
 */
public class Response505HTTPVersionNotSupported extends Response {

	public Response505HTTPVersionNotSupported(ClientThread client) {
		super(client, "505 HTTP Version Not Supported");
	}
}