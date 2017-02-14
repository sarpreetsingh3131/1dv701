package http.response;

import java.io.File;

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

	public enum ResponseType {OK, BAD_REQUEST, FORBIDDEN, NOT_FOUND, METHOD_NOT_SUPPORTED}
	
	public abstract ResponseType getResponseType();
	public abstract String getResponseString(boolean connection);
	public abstract String getContent();
	public abstract File getFile();

	protected String getConnection(boolean connection) {
		if (connection) {
			return "\r\n";
		}
		return "Connection: close\r\n\r\n";
	}

	protected String getContentLengthAndType(long length, String fileExtension) {
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