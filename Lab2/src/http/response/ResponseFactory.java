package http.response;

import java.io.FileNotFoundException;
import java.io.IOException;
import http.ServerThread;
import http.RequestParser;
import http.Method;
import http.Method.MethodType;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;

public class ResponseFactory {

	private Method method;
	private ServerThread client;

	public ResponseFactory(ServerThread client) {
		method = new Method();
		this.client = client;
	}

	/**
	 * This method get {@link MethodType} from {@link RequestParser} and then
	 * return appropriate {@link Response}. All the methods other than GET,
	 * POST, PUT are not implemented which means method will return 501
	 * response.
	 * 
	 * @param requestParser
	 * @return {@link Response}
	 */
	public Response getResponse(RequestParser requestParser) {

		switch (requestParser.getMethodType()) {

		case GET:
			try {
				return new Response200OK(client, method.GET(requestParser.getPath()));
			} catch (FileNotFoundException e) {
				return new Response404NotFound(client);
			} catch (SecurityException e) {
				return new Response403Forbidden(client);
			} catch (UnavailableForLegalReasonsException e) {
				return new Response451UnavailableForLegalReasons(client);
			} catch (LockedException e) {
				return new Response423Locked(client);
			}

		case POST:
			try {
				method.POST(requestParser);
				return new Response201Created(client, requestParser.getUploadedFileName());
			} catch (IOException e) {
				return new Response500InternalServerError(client);
			} catch (UnsupportedMediaTypeException e) {
				return new Response415UnsupportedMediaType(client);
			}

		default:
			return new Response501NotImplemented(client);
		}
	}

	/* Some handy methods for other responses. */
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

	public void writeResponse422UnprocssableEntity() {
		write(new Response422UnprocessableEntity(client));
	}

	private void write(Response response) {
		try {
			response.write();
		} catch (IOException e) {
		}
	}
}