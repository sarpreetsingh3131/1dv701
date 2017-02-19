package http.response;

import java.io.File;
import java.io.FileNotFoundException;
import http.ClientThread;
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
	private ClientThread client;
	
	public ResponseFactory(ClientThread client) {
		this.sharedFolder = new SharedFolder();
		this.client = client;
	}

	// Gets a response.
	public Response getResponse(Request request) {
		// returns depending on the request type.
		switch (request.getMethod()) {
		case GET:
			try {
				File file = sharedFolder.getFile(request.getPath());
				return new Response200OK(client, file);
			} catch (FileNotFoundException e) {
				return new Response404NotFound(client);
			} catch (SecurityException e) {
				return new Response403Forbidden(client);
			} catch (UnavailableForLegalReasonsException e) {
				return new Response451UnavailableForLegalReasons(client);
			} catch (LockedException e) {
				return new Response423Locked(client);
			} catch (InternalServerException e) {
				return new Response500InternalServerError(client);
			}
		default:
			return new Response501NotImplemented(client);
		}
	}

	public void writeResponse400BadRequest() {
		write(new Response400BadRequest(client));
	}

	public void writeResponse500InternalServerError() {
		write(new Response500InternalServerError(client));
	}

	public void writeResponse505HTTPVersionNotSupported() {
		write(new Response505HTTPVersionNotSupported(client));
	}

	public void writeResponse503ServiceUnavailable() {
		write(new Response503ServiceUnavailable(client));
	}

	public void writeResponse408RequestTimeout() {
		write(new Response408RequestTimeout(client));
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