package http.response;

import java.net.Socket;

public class Response429TooManyRequests extends Response {
	public Response429TooManyRequests(Socket socket) {
		super(socket, "429 Too Many Requests");
	}
	
}