package http.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import http.Request;
import http.SharedFolder;
import http.exceptions.InternalServerException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;

/*
 * This class returns Responses depending request.
 */
public class ResponseFactory {

	private SharedFolder sharedFolder;
	private Socket socket;
	private byte[] buffer;

	// Sets the directory to get the files from, socket and buffer.
	public ResponseFactory(SharedFolder sharedFolder, Socket socket, byte[] buffer) {
		this.sharedFolder = sharedFolder;
		this.socket = socket;
		this.buffer = buffer;
	}

	// Gets a response.
	public Response getResponse(Request request) {
		// returns depending on the request type.
		switch (request.getRequestType()) {
		case GET:
			try {
				File file = sharedFolder.getFile(request.getPath());
				return new Response200OK(file, socket, buffer);
			} catch (FileNotFoundException e) {
				return new Response404NotFound(socket);
			} catch (SecurityException e) {
				return new Response403Forbidden(socket);
			} catch (UnavailableForLegalReasonsException e) {
				return new Response451UnavailableForLegalReasons(socket);
			} catch (LockedException e) {
				return new Response423Locked(socket);
			} catch (InternalServerException e) {
				return new Response500InternalServerError(socket);
			}
		default:
			return new Response501NotImplemented(socket);
		}
	}

	public void writeResponse400BadRequest() {
		write(new Response400BadRequest(socket));
	}

	public void writeResponse500InternalServerError() {
		write(new Response500InternalServerError(socket));
	}

	public void writeResponse505HTTPVersionNotSupported() {
		write(new Response505HTTPVersionNotSupported(socket));
	}

	public void writeResponse503ServiceUnavailable() {
		write(new Response503ServiceUnavailable(socket));
	}

	public void writeResponse408RequestTimeout() {
		write(new Response408RequestTimeout(socket));
	}

	// writes the response.
	private void write(Response response) {
		try {
			response.write();
		} catch (InternalServerException e) {
			e.printStackTrace();
		}
	}
}