package http.response;

import http.ServerThread;

public class Response415UnsupportedMediaType extends Response {

	public Response415UnsupportedMediaType(ServerThread client) {
		super(client, "415 Unsupported Media Type",
				"The server does not support requested media type. It only support png and jpg/jpeg media types.");
	}
}
