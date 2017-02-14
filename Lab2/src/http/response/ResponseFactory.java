package http.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;

import http.Request;
import http.SharedFolder;

public class ResponseFactory {

	private SharedFolder sharedFolder;

	public ResponseFactory(SharedFolder sharedFolder) {
		this.sharedFolder = sharedFolder;
	}

	public Response getResponse(Request request) throws IOException {
		if (request.getRequestType() == Request.Type.GET) {
			try {
				File file = sharedFolder.getFile(request.getPath());
				return new Response200OK(file);
			} catch (FileNotFoundException e) {
				return new Response404NotFound();
			} catch (SecurityException e) {
				return new Response403Forbidden();
			} catch(SocketTimeoutException e){
				return new Response408Timeout();
			}
		}
		return new Response405MethodNotSupported(request.getRequestType());
	}
}