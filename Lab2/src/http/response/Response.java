package http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import http.ServerThread;

public abstract class Response {

	private String response;
	private final String CONTENT;
	private final String EXTENSION = "html";
	protected ServerThread client;

	public Response(ServerThread client, String header, String content) {
		this.response = "HTTP/1.1 " + header + "\r\n";
		this.CONTENT = "<html><body><h1>" + header + "</h1><p>" + content + "</p></body></html>";
		this.client = client;
	}

	/**
	 * This method writes a complete response to the client.
	 * 
	 * @throws IOException
	 *             If I/O occurs
	 */
	public void write() throws IOException {
		writeHeader(CONTENT.getBytes().length, EXTENSION);
		writeContent();
	}

	/**
	 * This method write the response header. It include date, content-length
	 * and file extension. Printer is set to auto flush that is why its true.
	 * 
	 * @param length
	 * @param fileExtension
	 * @throws IOException
	 */
	protected void writeHeader(long length, String fileExtension) throws IOException {

		response += "Date: " + new Date().toString() + "\r\n";
		response += "Content-Length: " + length + "\r\n";
		response += "Content-Type: " + getContentType(fileExtension) + "\r\n\r\n";

		PrintWriter printer = new PrintWriter(client.getSocket().getOutputStream(), true);
		printer.write(response);
		printer.flush();
	}

	/**
	 * This method write the content of the response which client will see in
	 * the browser such as requested files.
	 * 
	 * @throws IOException
	 */
	private void writeContent() throws IOException {

		client.getSocket().getOutputStream().write(CONTENT.getBytes());
	}

	/**
	 * This method find the suitable extension in the declared
	 * {@link ContentType} and return its value as a String. If fileExtension is
	 * not present in the {@link ContentType} then 'application unknown' will be
	 * returned.
	 * 
	 * @param length
	 * @param fileExtension
	 * @return String
	 */
	private String getContentType(String fileExtension) {

		for (ContentType type : ContentType.values()) {
			for (String extension : type.extensions) {
				if (fileExtension.equals(extension)) {
					return type.value;
				}
			}
		}

		return ContentType.applicationunknown.value;
	}

	
	/**
	 * 
	 * Enumeration for the Content Type
	 *
	 */
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

		/**
		 * We need constructor because we are setting the String value of
		 * Enumeration. extension is splitting in order to get all the declared
		 * values. Eg: text/html, html, htm are three different values separated
		 * by comma and We have to compare the given file extension value with
		 * each of it.
		 * 
		 * @param value
		 * @param extensions
		 */
		private ContentType(String value, String extensions) {
			this.value = value;
			this.extensions = extensions.split(", ");
		}

	}
}