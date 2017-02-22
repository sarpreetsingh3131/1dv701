package http.response;

import http.ServerThread;

/*
 * This class forms the specific 451 response.
 */
public class Response451UnavailableForLegalReasons extends Response {

	public Response451UnavailableForLegalReasons(ServerThread client) {
		super(client, "451 Unavailabe For Legal Reasons",
				"A server operator has received a legal demand to deny access to a resource or to a set of resources that includes the requested resource.");
	}
}
