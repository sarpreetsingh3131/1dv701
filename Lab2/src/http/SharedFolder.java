package http;

import java.io.File;
import java.io.FileNotFoundException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;

public class SharedFolder {

	private File sharedDirectory = new File("src/http/resources/inner");

	public synchronized File getFile(String path) throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException {
		if (path.endsWith("htm")) {
			path += "l";
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
		
		if(file.exists()) {
			switch(file.getName()) {
			case "secret.html": 	throw new SecurityException();
			case "legal.html" :		throw new UnavailableForLegalReasonsException();
			case "private.html" :	throw new LockedException();
			default: return file;
			}
		}
		
		throw new FileNotFoundException();
	}
}