package http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import http.exceptions.InternalServerException;

public abstract class Response {

	protected enum ContentType {
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
	
	protected Socket socket;
	
	public Response(Socket socket) {
		this.socket = socket;
	}
	
	public abstract void sendResponse() throws InternalServerException;
	
	protected void writeHeader(String header, long length, String fileExtension) throws InternalServerException {
		header += getContentLengthAndType(length, fileExtension);
		
		try {
			PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
			printer.write(header + "\r\n");
			printer.flush();
		} catch (Exception e) {
			throw new InternalServerException();
		}
	}
	
	protected void writeContent(String content) throws InternalServerException {
		try {
			socket.getOutputStream().write(content.getBytes());
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