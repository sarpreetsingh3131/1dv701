package http;

import java.io.File;
import java.io.FileNotFoundException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;

/*
 *  This class consist of the server directory and ability to return files.
 */
public class SharedFolder {

	private File sharedDirectory = new File("src/http/resources/inner");

	
	// Return the file for the path.
	public synchronized File getFile(String path) throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException {
		// Checks path to correctly return the index file.
		if (path.endsWith("htm")) {
			path += "l";
		} else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
			path += "/";
		}
		// Opens file
		File file = new File(sharedDirectory, path);
		// Checks if file is directory..
		if (file.isDirectory()) {
			// Checks for htm/html files inside the directory.
			for (int i = 0; i < file.listFiles().length; i++) {
				if (file.listFiles()[i].getName().contains("htm")) {
					file = file.listFiles()[i];
					break;
				}
			}
		}
		
		// If file exists then checks for certain restrictions, if not then throw 404.
		if(file.exists()) {
			// Checks the 403, 451 and 423, otherwise return the file.
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