package http.response;

import http.ServerThread;

/*
 * This class forms the specific 451 response.
 */
public class Response451UnavailableForLegalReasons extends Response {

	public Response451UnavailableForLegalReasons(ServerThread client) {
		super(client, "451 Unavailabe For Legal Reasons");
	}
}
