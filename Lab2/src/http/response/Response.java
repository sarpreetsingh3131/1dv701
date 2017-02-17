package http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import http.exceptions.InternalServerException;

/*
 * This class consist of one Response and ability to send it.
 */
public abstract class Response {
	
	private enum ContentType {
		// Types of the content in the response.
		texthtml("text/html", "html, htm"), 
		textcss("text/css", "css"), 
		textjavascript("text/javascript", "js"), 
		imagepng("image/png", "png"), 
		imagegif("image/gif", "gif"), 
		imagejpeg("image/jpeg", "jpg, jpeg"), 
		applicationunknown("application/unknown", "*");

		private String value;
		private String[] extensions;

		// Sets content value and extension.
		private ContentType(String value, String extensions) {
			this.value = value;
			this.extensions = extensions.split(", ");
		}
	}
	
	private String response;
	private final String CONTENT;
	private final String EXTENSION = "html";
	protected Socket socket;

	// Forms the response.
	public Response(Socket socket, String response) {
		this.response = "HTTP/1.1 " + response + "\r\n";
		this.CONTENT = "<html><body><h1>" + response + "</h1></body></html>";
		this.socket = socket;
	}

	// Write/sends the response.
	public void write() throws InternalServerException{
		writeHeader(CONTENT.getBytes().length, EXTENSION);
		writeContent();
	}

	// Forms the header.
	protected void writeHeader(long length, String fileExtension) throws InternalServerException {
		// Includes date, content length and type.
		response += "Date: " + new Date().toString() + "\r\n";
		response += getContentLengthAndType(length, fileExtension) + "\r\n";
		
		// Sends the header.
		try {
			PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
			printer.write(response);
			printer.flush();
		} catch (Exception e) {
			throw new InternalServerException();
		}
	}

	// Sends the content body.
	private void writeContent() throws InternalServerException {
		try {
			socket.getOutputStream().write(CONTENT.getBytes());
		} catch (IOException e) {
			throw new InternalServerException();
		}
	}
	
	// Returns the content length plus the type.
	private String getContentLengthAndType(long length, String fileExtension) {
		// Checks if the content type is known.
		for (ContentType type : ContentType.values()) {
			for (String extension : type.extensions) {
				if (fileExtension.equals(extension)) {
					return "Content-Length: " + length + "\r\nContent-Type: " + type.value + "\r\n";
				}
			}
		}
		//else return the application unknown.
		return "Content-Length: " + length + "\r\nContent-Type: " + ContentType.applicationunknown.value + "\r\n";
	}
}