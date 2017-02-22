package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import http.exceptions.BadRequestException;
import http.exceptions.ConflictException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;

public class Method {

	// This path separator only works on mac. Other users must change it.
	private final String PATH_SEPARATOR = "/";

	public enum MethodType {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	private final String PATH = "src" + PATH_SEPARATOR + "http" + PATH_SEPARATOR + "resources" + PATH_SEPARATOR + "inner";
	private final File SHARED_FOLDER = new File(PATH);
	private final String ALLOWED_MEDIA_TYPE = "png/jpg/jpeg";

	/**
	 * This method returns the requested File when user do GET request. If path
	 * ends with '/' then it return default file or if ends with 'htm' then it
	 * include 'l' in the path. If path does not contain '/' and '.'
	 * (Eg:localhost:8080) then it include '/' in it. Then it create a file
	 * using the shared folder as parent and given path as child. If file is
	 * directory then we look for index file. If file is not directory and it
	 * exits then it return the suitable Exception or File. Otherwise, it throw
	 * FileNotFoundException. NOTE: Only default directory have index file, all
	 * the sub directories have no index file which results in
	 * FileNotFoundException.
	 * 
	 * @param path
	 * @return File
	 * @throws FileNotFoundException
	 *             When file is not present in the directory or given path
	 *             belong to directory rather than a file
	 * @throws UnavailableForLegalReasonsException
	 *             When path belong to legal directory
	 * @throws LockedException
	 *             When path belong to private directory
	 * @throws SecurityException
	 *             When path belong to secret directory
	 */
	public File GET(String path) throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException, SecurityException {

		if (path.equals("/")) {
			path += "index.html";
		}

		else if (path.endsWith("htm")) {
			path += "l";
		}

		else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
			path += "/";
		}

		File file = new File(SHARED_FOLDER, path);

		if (file.isDirectory()) {
			for (int i = 0; i < file.listFiles().length; i++) {
				if (file.listFiles()[i].getName().startsWith("index.htm")) {
					file = file.listFiles()[i];
					break;
				}
			}
		}

		if (!file.isDirectory() && file.exists()) {
			switch (file.getParent()) {
			case PATH + PATH_SEPARATOR + "secret":
				throw new SecurityException();
			case PATH + PATH_SEPARATOR + "legal":
				throw new UnavailableForLegalReasonsException();
			case PATH + PATH_SEPARATOR + "private":
				throw new LockedException();
			default:
				return file;
			}
		}
		throw new FileNotFoundException();
	}

	/**
	 * This method handle POST request by first checking if uploaded file
	 * extension is allowed, then it convert the request body into byte array
	 * using inbuilt library. At last, create a new file and write it using
	 * FileOutputStream.
	 * 
	 * @param requestParser
	 * @throws IOException
	 *             When writing a new file got error
	 * @throws UnsupportedMediaTypeException
	 *             When uploaded file is other than png/jpg/jpeg format.
	 */

	public void POST(RequestParser requestParser) throws IOException, UnsupportedMediaTypeException {

		if (!ALLOWED_MEDIA_TYPE.contains(requestParser.getUploadedFileExtension())) {
			throw new UnsupportedMediaTypeException();
		}

		byte[] imageBytes = DatatypeConverter.parseBase64Binary(requestParser.getBody());

		File imageFile = new File(SHARED_FOLDER,
				PATH_SEPARATOR + "images" + PATH_SEPARATOR + requestParser.getUploadedFileName());

		FileOutputStream fop = new FileOutputStream(imageFile, false);
		fop.write(imageBytes);
		fop.flush();
		fop.close();
	}

	/**
	 * This method handle PUT request. It first checks if file exists(delete if
	 * exist) and then overwrite the file using POST method.
	 * 
	 * @param requestParser
	 * @throws ConflictException
	 *             If file does not exist
	 * @throws IOException
	 * @throws UnsupportedMediaTypeException
	 *             If given file is not png/jpg/jpeg
	 */
	public void PUT(RequestParser requestParser) throws ConflictException, IOException, UnsupportedMediaTypeException {

		File file = new File(SHARED_FOLDER,
				PATH_SEPARATOR + "images" + PATH_SEPARATOR + requestParser.getUploadedFileName());

		if (!file.exists()) {
			throw new ConflictException();
		}

		file.delete();

		POST(requestParser);
	}

	/**
	 * This method convert the given method into {@link MethodType}.
	 * 
	 * @param method
	 * @return {@link MethodType}
	 * @throws BadRequestException
	 *             If given parameter value is not present in
	 *             {@link MethodType}.
	 */
	public static MethodType getEnumMethodType(String method) throws BadRequestException {

		for (MethodType m : MethodType.values()) {
			if (method.equals(m.name())) {
				return m;
			}
		}
		throw new BadRequestException();
	}
}