package http.response;

import java.io.FileNotFoundException;
import java.io.IOException;
import http.ServerThread;
import http.RequestParser;
import http.Method;
import http.Method.MethodType;
import http.exceptions.ConflictException;
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
	 * @throws FileNotFoundException
	 *             If file is not found
	 * @throws SecurityException
	 *             If file is found but it is forbidden
	 * @throws UnavailableForLegalReasonsException
	 *             If file is found but it is unavailable
	 * @throws LockedException
	 *             If file is found but it is locked
	 * @throws IOException
	 *             I/O while writing file to to client
	 * @throws UnsupportedMediaTypeException
	 *             If uploaded file is not png/jpg/jpeg type
	 * @throws ConflictException
	 *             If file does not exist and client made PUT request to update
	 *             it
	 */
	public Response getResponse(RequestParser requestParser)
			throws IOException, ConflictException, UnsupportedMediaTypeException, LockedException,
			UnavailableForLegalReasonsException, FileNotFoundException, SecurityException {

		switch (requestParser.getMethodType()) {

		case GET:
			return new Response200OK(client, method.GET(requestParser.getPath()));

		case POST:
			method.POST(requestParser);
			return new Response201Created(client, requestParser.getUploadedFileName());

		case PUT:
			method.PUT(requestParser);
			return new Response204NoContent(client);

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

	public void writeResponse404NotFound() {
		write(new Response404NotFound(client));
	}

	public void writeResponse403Forbidden() {
		write(new Response403Forbidden(client));
	}

	public void writeResponse451UnavailableForLegalReasons() {
		write(new Response451UnavailableForLegalReasons(client));
	}

	public void writeResponse415UnsupportedMediaType() {
		write(new Response415UnsupportedMediaType(client));
	}

	public void writeResponse409Conflict(String filename) {
		write(new Response409Conflict(client, filename));
	}

	public void writeResponse423Locked() {
		write(new Response423Locked(client));
	}

	// WRITER
	private void write(Response response) {
		try {
			response.write();
		} catch (IOException e) {
		}
	}
}
