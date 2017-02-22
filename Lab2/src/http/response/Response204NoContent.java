package http.response;

import http.ServerThread;

public class Response204NoContent extends Response {

	/**
	 * This response will be shown when user will update the existing file.
	 * Because it is 204 which means no content, so nothing will be displayed in
	 * the browser and 204 will be send to the client.
	 * 
	 * @param client
	 */
	public Response204NoContent(ServerThread client) {
		super(client, "204 No Content", "");
	}
}