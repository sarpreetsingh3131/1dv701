package http.response;

import http.ServerThread;

public class Response422UnprocessableEntity extends Response {

	public Response422UnprocessableEntity(ServerThread client) {
		super(client, "422 Unprocessable Entity", "The request was well-formed but was unable to be followed due to semantic errors.");
	}
}
