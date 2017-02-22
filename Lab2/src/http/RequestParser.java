package http;

import java.io.BufferedReader;
import java.io.IOException;
import http.Method.Type;
import http.exceptions.BadRequestException;
import http.exceptions.VersionNotSupportedException;

/*
 * A class containing data about for request.
 */
public class RequestParser {

	private Type type;
	private String path;
	private final String HTTP_VERSION = "HTTP/1.1";
	private boolean connectionClosed = false;
	private long contentLength = 0;
	private String body;
	private String uploadedFileName;
	private String uploadedFileExtension;

	public RequestParser() {
	}

	public RequestParser parse(BufferedReader reader)
			throws BadRequestException, IOException, VersionNotSupportedException {

		parseHeader(readHeader(reader));
		
		//Otherwise its GET only
		if(contentLength > 0) parseBody(readBody(reader)); 

		return new RequestParser(type, path, body, connectionClosed, uploadedFileName, uploadedFileExtension);
	}

	private void parseHeader(String header) throws BadRequestException, VersionNotSupportedException, ArrayIndexOutOfBoundsException {
	
		// splits header types.
		String[] totalLines = header.split("\r\n");

		// splits first line.
		String[] request = totalLines[0].split(" ");
		
		// Checks if length is 3 (GET / HTTP/1.1), if not then throw 400.
		if (request.length != 3) {
			throw new BadRequestException();
		}

		// Checks the right HTTP version or throw 505.
		if (!request[2].equals(HTTP_VERSION)) {
			throw new VersionNotSupportedException();
		}

		// Connection: close || Connection: keep-alive
		for (int i = 1; i < totalLines.length; i++) {
			if (totalLines[i].startsWith("Connection")) {
				this.connectionClosed = totalLines[i].split(": ")[1].equals("close");
				break;
			}
		}

		this.type = Method.getEnumMethodType(request[0]);
		this.path = request[1];
	}

	private String readHeader(BufferedReader reader) throws BadRequestException, IOException {

		StringBuilder header = new StringBuilder();

		while (true) {
			
			String line  = reader.readLine();
		
			if (line == null || line.equals("\r\n") || line.isEmpty() || line.equals("")) {
				break;
			}
			
			header.append(line + "\r\n");

			if (line.startsWith("Content-Length")) {
				try {
					// "Content-Length: ".length() = 16
					contentLength = Integer.parseInt(line.substring(16));
				} catch (NumberFormatException e) {
					throw new BadRequestException();
				}
			}
		}
		return header.toString();
	}

	private void parseBody(String body) throws ArrayIndexOutOfBoundsException {

		this.uploadedFileName = body.split("=")[0];
		this.body = body.split("base64,")[1];
		this.uploadedFileExtension = body.split(":")[1].split(";")[0].split("/")[1];
	}

	private String readBody(BufferedReader reader) throws IOException {

		StringBuilder body = new StringBuilder();
		for (int i = 0; i < contentLength; i++) {
			char ch = (char) reader.read();

			// change from hexa to char, i+=2 -> we already read next two chars
			if (ch == '%') {
				ch = (char) Integer.parseInt("" + (char) reader.read() + "" + (char) reader.read(), 16);
				i += 2;
			}
			body.append(ch);
		}

		return body.toString();
	}

	private RequestParser(Type type, String path, String body, boolean connectionClosed, String uploadedFileName,
			String uploadedFileExtension) {

		this.type = type;
		this.path = path;
		this.body = body;
		this.connectionClosed = connectionClosed;
		this.uploadedFileName = uploadedFileName;
		this.uploadedFileExtension = uploadedFileExtension;
	}

	public Type getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public boolean connectionClosed() {
		return connectionClosed;
	}

	public String getBody() {
		return body;
	}

	public String getUploadedFileName() {
		return uploadedFileName;
	}

	public String getUploadedFileExtension() {
		return uploadedFileExtension;
	}
}