package http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import http.ServerThread;

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
			//Get all declared extensions separated by comma
			this.extensions = extensions.split(", ");
		}
	}

	private String response;
	private final String CONTENT;
	private final String EXTENSION = "html";
	protected ServerThread client;

	// Forms the response.
	public Response(ServerThread client, String header, String content) {
		this.response = "HTTP/1.1 " + header + "\r\n";
		this.CONTENT = "<html><body><h1>" + header + "</h1><p>" + content + "</p></body></html>";
		this.client = client;
	}

	// Write/sends the response.
	public void write() throws IOException {
		writeHeader(CONTENT.getBytes().length, EXTENSION);
		writeContent();
	}

	// Forms the header.
	protected void writeHeader(long length, String fileExtension) throws IOException {
		// Includes date, content length and type.
		response += "Date: " + new Date().toString() + "\r\n";
		response += getContentLengthAndType(length, fileExtension) + "\r\n";

		// Sends the header.
		PrintWriter printer = new PrintWriter(client.getSocket().getOutputStream(), true);
		printer.write(response);
		printer.flush();
	}

	// Sends the content body.
	private void writeContent() throws IOException {
		client.getSocket().getOutputStream().write(CONTENT.getBytes());
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
		// else return the application unknown.
		return "Content-Length: " + length + "\r\nContent-Type: " + ContentType.applicationunknown.value + "\r\n";
	}
}