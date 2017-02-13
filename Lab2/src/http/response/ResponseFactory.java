package http.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
				File file = sharedFolder.getURL(request.getURL());
				return new Response200OK(file);
			}catch (FileNotFoundException e) {
				return new Response404NotFound();
			} catch (SecurityException e) {
				return new Response403Forbidden();
			} 
		}
		return new Response405MethodNotSupported(request.getRequestType());
	}
}