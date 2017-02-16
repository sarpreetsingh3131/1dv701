package http.response;

import java.net.Socket;

public class Response501NotImplemented extends Response {

	public Response501NotImplemented(Socket socket) {
		super(socket,"501 Not Implemented","501 Not Implemented","html");
	}

}