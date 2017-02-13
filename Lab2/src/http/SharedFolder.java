package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SharedFolder {

	private File sharedDirectory = new File("src/http/resources/inner");

	public synchronized File getURL(String path) throws IOException {
		if (path.endsWith("htm")) {
			path = path.split("\\.")[0] + ".html";
		} else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
			path += "/";
		}

		File file = new File(sharedDirectory, path);

		if (file.isDirectory()) {
			for (int i = 0; i < file.listFiles().length; i++) {
				if (file.listFiles()[i].getName().contains("htm")) {
					file = file.listFiles()[i];
					break;
				}
			}
		}

		if (file.getName().equals("secret.html")) {
			throw new SecurityException();
		}
		if (file.exists()) {
			return file;
		}
		throw new FileNotFoundException();
	}
}