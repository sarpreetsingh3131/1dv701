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
import http.exceptions.InternalServerException;
import http.exceptions.LockedException;
import http.exceptions.UnavailableForLegalReasonsException;
import http.exceptions.UnsupportedMediaTypeException;

public class Method {

	public enum Type {GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH}
	private final File SHARED_FOLDER = new File("src/http/resources/inner");
	private final String ALLOWED_MEDIA_TYPE = "png";
	private static int imageCounter = 0;

	// synchronized so that a user get all the files present in a html page
	public synchronized File GET(String path) throws FileNotFoundException, UnavailableForLegalReasonsException,
			LockedException, InternalServerException {

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

		if (file.isDirectory()) {
			throw new InternalServerException();
		}

		if (file.exists()) {
			// For all files(even if add new one) in these folder
			switch (file.getParent()) {
			case "src/http/resources/inner/secret":  throw new SecurityException();
			case "src/http/resources/inner/legal":   throw new UnavailableForLegalReasonsException();
			case "src/http/resources/inner/private": throw new LockedException();
			default: return file;
			}
		}
		throw new FileNotFoundException();
	}

	
	public synchronized void POST(String content) throws IOException, UnsupportedMediaTypeException {
		if (content.isEmpty()) {
			System.out.println("EMPTY PARAMETER");
			// TODO NEED TO ASK THE TEACHER
		}

		String body = content.split(",")[1];
		String extension = content.split(":")[1].split(";")[0].split("/")[1];
		if (!extension.equals(ALLOWED_MEDIA_TYPE)) {
			throw new UnsupportedMediaTypeException();
		}
		
		// convert base64 string to byte array
		byte[] imageBytes = DatatypeConverter.parseBase64Binary(body);
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage image = ImageIO.read(in);
		File imageFile = new File(SHARED_FOLDER, "/images/img" + (++imageCounter) + "." + extension);

		// check for duplicate
		while (imageFile.exists()) {
			imageFile = new File(SHARED_FOLDER, "/images/img" + (++imageCounter) + "." + extension);
		}
		ImageIO.write(image, extension, imageFile);
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