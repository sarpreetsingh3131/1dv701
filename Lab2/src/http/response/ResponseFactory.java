package http.response;

import java.io.FileNotFoundException;
import java.io.IOException;
import http.ClientThread;
import http.PostMethod;
import http.Request;
import http.SharedFolder;
import http.exceptions.InternalServerException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;

/*
 * This class returns Responses depending request.
 */
public class ResponseFactory {

	private SharedFolder sharedFolder;
	private ClientThread client;
	private PostMethod post;
	
	public ResponseFactory(ClientThread client) {
		sharedFolder = new SharedFolder();
		post = new PostMethod();
		this.client = client;
	}

	public Response getResponse(Request request) {
		switch (request.getMethod()) {
		case GET:
			try {
				return new Response200OK(client, sharedFolder.getFile(request.getPath()));
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
		case POST:
			try {
				post.saveFile(request.getBody(), sharedFolder.getImagesFolder());
				return new Response201Created(client);
			} catch (IOException e) {
				return new Response500InternalServerError(client);
			} catch (UnsupportedMediaTypeException e) {
				return new Response415UnsupportedMediaType(client);
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
	
	public void writeResponse413RequestEntityTooLarge() {
		write(new Response413RequestEntityTooLarge(client));
	}

	// writes the response.
	private void write(Response response) {
		try {
			response.write();
		} catch (IOException e) {
		}
	}
}