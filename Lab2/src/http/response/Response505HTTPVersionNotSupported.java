package http.response;

import http.ServerThread;

public class Response505HTTPVersionNotSupported extends Response {

	public Response505HTTPVersionNotSupported(ServerThread client) {
		super(client, "505 HTTP Version Not Supported",
				"The server does not support the HTTP protocol version used in the request. Only 1.1 is supported by the server.");
	}
}