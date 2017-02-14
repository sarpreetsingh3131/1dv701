package http.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import http.Request;
import http.SharedFolder;
import http.exceptions.InternalServerException;

public class ResponseFactory {

	private SharedFolder sharedFolder;
	private Socket socket;
	private byte[] buffer;

	public ResponseFactory(SharedFolder sharedFolder, Socket socket, byte[] buffer) {
		this.sharedFolder = sharedFolder;
		this.socket = socket;
		this.buffer = buffer;
	}

	public Response getResponse(Request request) {
		if (request.getRequestType() == Request.Type.GET) {
			try {
				File file = sharedFolder.getFile(request.getPath());
				return new Response200OK(file, socket, buffer);
			} catch (FileNotFoundException e) {
				return new Response404NotFound(socket);
			} catch (SecurityException e) {
				return new Response403Forbidden(socket);
			}
		}
		return new Response405MethodNotSupported(request.getRequestType(), socket);
	}

	public void writeResponse400BadRequest() {
		write(new Response400BadRequest(socket));
	}

	public void writeResponse500InternalServerError() {
		write(new Response500InternalServerError(socket));
	}

	private void write(Response response) {
		try {
			response.sendResponse();
		} catch (InternalServerException e) {
			e.printStackTrace();
		}
	}
}