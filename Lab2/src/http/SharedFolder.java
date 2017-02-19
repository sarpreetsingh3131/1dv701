package http;

import java.io.File;
import java.io.FileNotFoundException;
import http.exceptions.InternalServerException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;

/*
 *  This class consist of the server directory and ability to return files.
 */
public class SharedFolder {

	private File sharedDirectory = new File("src/http/resources/inner");

	// Return the file for the path.
	public synchronized File getFile(String path) throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException, InternalServerException {
		//default file
		if(path.equals("/")) {
			path += "index.html";
		}
		
		else if (path.endsWith("htm")) {
			path += "l";
		} 
		
		//If path does not ends with back slash and contains no dot(.) 
		else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
			path += "/";
		}
	
		File file = new File(sharedDirectory, path);
		
		if (file.isDirectory()) {
			throw new InternalServerException();
		}
		
		// If file exists then checks for certain restrictions, if not then throw 404.
		if(file.exists()) {
			//All files(even if add new one) in these folder must show correct response
			switch(file.getParent()) {
			case "src/http/resources/inner/secret": 	throw new SecurityException();
			case "src/http/resources/inner/legal":		throw new UnavailableForLegalReasonsException();
			case "src/http/resources/inner/private":	throw new LockedException();
			default: return file;
			}
		}
		throw new FileNotFoundException();
	}
}