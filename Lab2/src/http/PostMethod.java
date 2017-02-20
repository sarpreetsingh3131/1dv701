package http;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import http.exceptions.UnsupportedMediaTypeException;

public class PostMethod {
	
	private static int count = 0;
	private final String MEDIA_TYPE = "png";
	
	public void saveFile(String content, File imageFolder) throws IOException, UnsupportedMediaTypeException {
		if(content.isEmpty()) {
			System.out.println("EMPTY PARAMETER");
			//TODO NEED TO ASK THE TEACHER
		}
		
		String body = content.split(",")[1];
		String extension = content.split(":")[1].split(";")[0].split("/")[1];
		if(!extension.equals(MEDIA_TYPE)) {
			throw new UnsupportedMediaTypeException();
		}
		//convert to base64
		byte[] imageBytes = DatatypeConverter.parseBase64Binary(body);
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage image = ImageIO.read(in);
		File imageFile = new File(imageFolder, "/img" + (++count) + "." + extension);
		
		// check for duplicate
		while(imageFile.exists()) {
			imageFile = new File(imageFolder, "/img" + (++count) + "." + extension);
		}
		ImageIO.write(image, extension, imageFile);
	}
}