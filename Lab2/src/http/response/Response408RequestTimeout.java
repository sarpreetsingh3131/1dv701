package http.response;

import http.ClientThread;

/*
 * This class forms the specific 408 response.
 */
public class Response408RequestTimeout extends Response {

	public Response408RequestTimeout(ClientThread client) {
		super(client, "408 Request Time-out");
	}
}
