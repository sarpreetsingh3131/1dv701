package http.response;

import http.ClientThread;

/*
 * This class forms the specific 451 response.
 */
public class Response451UnavailableForLegalReasons extends Response {

	public Response451UnavailableForLegalReasons(ClientThread client) {
		super(client, "451 Unavailabe For Legal Reasons");
	}
}
