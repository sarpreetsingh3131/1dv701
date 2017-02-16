package http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import http.exceptions.InternalServerException;

public abstract class Response {
	
	private enum ContentType {
		texthtml("text/html", "html, htm"), 
		textcss("text/css", "css"), 
		textjavascript("text/javascript", "js"), 
		imagepng("image/png", "png"), 
		imagegif("image/gif", "gif"), 
		imagejpeg("image/jpeg", "jpg, jpeg"), 
		applicationunknown("application/unknown", "*");

		private String value;
		private String[] extensions;

		private ContentType(String value, String extensions) {
			this.value = value;
			this.extensions = extensions.split(", ");
		}
	}
	
	private String response;
	private final String CONTENT;
	private final String EXTENSION = "html";
	protected Socket socket;

	public Response(Socket socket, String response) {
		this.response = "HTTP/1.1 " + response + "\r\n";
		this.CONTENT = "<html><body><h1>" + response + "</h1></body></html>";
		this.socket = socket;
	}

	public void write() throws InternalServerException{
		writeHeader(CONTENT.getBytes().length, EXTENSION);
		writeContent();
	}

	protected void writeHeader(long length, String fileExtension) throws InternalServerException {
		response += "Date: " + new Date().toString() + "\r\n";
		response += getContentLengthAndType(length, fileExtension) + "\r\n";
		
		try {
			PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
			printer.write(response);
			printer.flush();
		} catch (Exception e) {
			throw new InternalServerException();
		}
	}

	private void writeContent() throws InternalServerException {
		try {
			socket.getOutputStream().write(CONTENT.getBytes());
		} catch (IOException e) {
			throw new InternalServerException();
		}
	}

	private String getContentLengthAndType(long length, String fileExtension) {
		for (ContentType type : ContentType.values()) {
			for (String extension : type.extensions) {
				if (fileExtension.equals(extension)) {
					return "Content-Length: " + length + "\r\nContent-Type: " + type.value + "\r\n";
				}
			}
		}
		return "Content-Length: " + length + "\r\nContent-Type: " + ContentType.applicationunknown.value + "\r\n";
	}
}