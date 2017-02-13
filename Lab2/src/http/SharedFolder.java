package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SharedFolder {

	private File sharedDirectory = new File("src/http/resources/inner");

	public synchronized File getURL(String path) throws IOException {
		if (path.endsWith("/")) {
			path += "index.html";
		}
		File file = new File(sharedDirectory, path);
		if (file.getName().equals("secret.html")) {
			throw new SecurityException();
		}
		if (file.exists()) {
			return file;
		}
		throw new FileNotFoundException();
	}
}