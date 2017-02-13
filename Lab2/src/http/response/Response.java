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
		private String[] fileExtensions;

		private ContentType(String value, String fileExtensions) {
			this.value = value;
			this.fileExtensions = fileExtensions.split(", ");
		}

		private String getValue() {
			return value;
		}
	}
	
	public enum ResponseType {OK, BAD_REQUEST, FORBIDDEN, NOT_FOUND, METHOD_NOT_SUPPORTED}
	
	public abstract ResponseType getResponseType();
	
	public abstract String getResponseString(boolean connection);

	public abstract String getContent();
	
	public abstract File getFile();
	
	protected String getContentLength(long length) {
		return "Content-Length: " + length + "\r\n";
	}

	protected String getContentType(ContentType type) {
		return "Content-Type: " + type.getValue() + "\r\n";
	}

	protected String getConnection(boolean connection) {
		if (connection) {
			return "\r\n";
		}
		return "Connection: close\r\n\r\n";
	}
	
	protected ContentType getContentType(String fileExtension) {
		for(ContentType type: ContentType.values()) {
			for(String extension: type.fileExtensions) {
				if(fileExtension.equals(extension)) {
					return type;
				}
			}
		}
		return ContentType.applicationunknown;
	}
}