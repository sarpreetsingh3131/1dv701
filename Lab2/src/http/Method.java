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

	public enum Type {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}

	/**
	 *  Please change the path separator according to OS. This only works on mac.
	 */
	private final String PATH_SEPARATOR = "/";

	private final String PATH = "src" + PATH_SEPARATOR + "http" + PATH_SEPARATOR + "resources" + PATH_SEPARATOR + "inner";
	private final File SHARED_FOLDER = new File(PATH);
	private final String ALLOWED_MEDIA_TYPE = "png/jpg/jpeg";

	public synchronized File GET(String path) throws FileNotFoundException, UnavailableForLegalReasonsException, LockedException {

		if (path.equals("/")) {
			path += "index.html"; // default file
		}

		else if (path.endsWith("htm")) {
			path += "l";
		}

		// If path does not ends with back slash and contains no dot(.)
		else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
			path += "/";
		}

		File file = new File(SHARED_FOLDER, path);

		// Because these directories don't have index file, so show 404 else file
		if (!file.isDirectory() && file.exists()) {
			switch (file.getParent()) {
			case PATH + PATH_SEPARATOR + "secret":	 	throw new SecurityException();
			case PATH + PATH_SEPARATOR + "legal":   	throw new UnavailableForLegalReasonsException();
			case PATH + PATH_SEPARATOR + "private": 	throw new LockedException();
			default: return file;
			}
		}
		throw new FileNotFoundException();
	}

	public void POST(RequestParser request) throws IOException, UnsupportedMediaTypeException {

		if (!ALLOWED_MEDIA_TYPE.contains(request.getUploadedFileExtension())) {
			throw new UnsupportedMediaTypeException();
		}

		// convert base64 string to byte array
		byte[] imageBytes = DatatypeConverter.parseBase64Binary(request.getBody());
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage image = ImageIO.read(in);

		File imageFile = new File(SHARED_FOLDER, PATH_SEPARATOR +"images" + PATH_SEPARATOR + request.getUploadedFileName());
		ImageIO.write(image, request.getUploadedFileExtension(), imageFile);
	}

	public static Type getEnumMethodType(String method) throws BadRequestException {
		for (Type m : Type.values()) {
			if (method.equals(m.name())) {
				return m;
			}
		}
		// If the type does not exist then throw 400.
		throw new BadRequestException();
	}
}