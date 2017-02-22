package http;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import http.exceptions.BadRequestException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;

public class Method {

	// This path separator only works on mac. Other users must change it.
	private final String PATH_SEPARATOR = "/";

	public enum MethodType {
		GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH
	}

	private final String PATH = "src" + PATH_SEPARATOR + "http" + PATH_SEPARATOR + "resources" + PATH_SEPARATOR
			+ "inner";
	private final File SHARED_FOLDER = new File(PATH);
	private final String ALLOWED_MEDIA_TYPE = "png/jpg/jpeg";

	/**
	 * This method returns the requested File when user do GET request. If path
	 * ends with '/' then it return default file or if ends with 'htm' then it
	 * include 'l' in the path. If path does not contain '/' and '.'
	 * (Eg:localhost:8080) then it include '/' in it. Then it create a file
	 * using the shared folder as parent and given path as child. If file is not
	 * directory and it exits then it return the suitable Exception or File.
	 * Otherwise, it throw FileNotFoundException. NOTE: Only default directory
	 * have index file, all the sub directories have no index file which results
	 * in FileNotFoundException.
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
	public File GET(String path)
			throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException, SecurityException {

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
	 * This method first check if uploaded file extension is allowed, then it
	 * convert the request body into byte array using inbuilt library. Then it
	 * create an input stream using byte array and use this stream to create
	 * buffered image. At last, create a new file and write it.
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
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage image = ImageIO.read(in);

		File imageFile = new File(SHARED_FOLDER,
				PATH_SEPARATOR + "images" + PATH_SEPARATOR + requestParser.getUploadedFileName());

		ImageIO.write(image, requestParser.getUploadedFileExtension(), imageFile);
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