package http;

import java.io.BufferedReader;
import java.io.IOException;
import http.Method.MethodType;
import http.exceptions.BadRequestException;
import http.exceptions.VersionNotSupportedException;

public class RequestParser {

	private MethodType type;
	private String path;
	private final String HTTP_VERSION = "HTTP/1.1";
	private boolean connectionClosed = false;
	private long contentLength = 0;
	private String body;
	private String uploadedFileName;
	private String uploadedFileExtension;

	public RequestParser() {
	}

	/**
	 * This method parse the header and body using private method. If content
	 * length is more than 0 means its POST or PUT request (GET does not content
	 * length) then read the body. At last return a new RequestParser containing
	 * all the files which we have assigned while reading the header and/or
	 * body.
	 * 
	 * @return {@link RequestParser}
	 */
	public RequestParser parse(BufferedReader reader)
			throws BadRequestException, IOException, VersionNotSupportedException {

		parseHeader(readHeader(reader));

		if (contentLength > 0) {
			parseBody(readBody(reader));
		}

		return new RequestParser(type, path, body, connectionClosed, uploadedFileName, uploadedFileExtension);
	}

	/**
	 * This method parse the request header. After reading the header from
	 * private method we get a string which we split by tabs and new line
	 * because we have added these while reading in the private method. Now we
	 * have to check what is request type, path and version, so we split the
	 * first index using " " because data is in this format [GET / HTTP/1.1,
	 * Host: localhost:8080,.....]. From this split we get a new array which
	 * looks like [GET, /, HTTP/1.1]. If length is not 3 it is bad request.
	 * Otherwise we check version if its 1.1 and connection if its keep-alive or
	 * close. At last we assign the values to class attributes.
	 * 
	 * @param header
	 * @throws BadRequestException
	 *             If request is not valid. Eg: if its GETS instead of GET
	 *             and/or missing the path and version
	 * @throws VersionNotSupportedException
	 *             If HTTP in not 1.1
	 * @throws ArrayIndexOutOfBoundsException
	 *             If header content is not in same pattern as we decided
	 * @throws NumberFormatException
	 *             If content length is not number
	 */
	private void parseHeader(String header) throws BadRequestException, VersionNotSupportedException,
			ArrayIndexOutOfBoundsException, NumberFormatException {

		String[] totalLines = header.split("\r\n");

		String[] request = totalLines[0].split(" ");

		if (request.length != 3) {
			throw new BadRequestException();
		}

		if (!request[2].equals(HTTP_VERSION)) {
			throw new VersionNotSupportedException();
		}

		for (int i = 1; i < totalLines.length; i++) {
			if (totalLines[i].startsWith("Connection")) {
				this.connectionClosed = totalLines[i].split(": ")[1].equals("close");
				break;
			}
		}

		this.type = Method.getEnumMethodType(request[0]);
		this.path = request[1];
	}

	/**
	 * This method read the header and stops when the line is empty or null
	 * because we know that there is one empty line between header and body. For
	 * differentiating the header we are adding a tab and a new line after each
	 * header property. We know that in POST and PUT request we get
	 * content-length so we are saving it if present in the header property for
	 * reading the body correctly later. Substring is 16 because
	 * "Content-Length: ".length() = 16.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 *             If I/O occurs while reading
	 * @throws NumberFormatException
	 *             If content length is not number
	 */
	private String readHeader(BufferedReader reader) throws IOException, NumberFormatException {

		StringBuilder header = new StringBuilder();

		while (true) {

			String line = reader.readLine();

			if (line == null || line.equals("\r\n") || line.isEmpty() || line.equals("")) {
				break;
			}

			header.append(line + "\r\n");

			if (line.startsWith("Content-Length")) {
				contentLength = Integer.parseInt(line.substring(16));
			}
		}

		return header.toString();
	}

	/**
	 * This method parse the request body. For getting the file name, body and
	 * extension we have to split the string in a specific way because we know
	 * that the data will come in this format <image name>=<data:image/<image
	 * extension>;base64,>,<image data>
	 * 
	 * @param body
	 * @throws ArrayIndexOutOfBoundsException
	 *             If body content is not in same pattern as we decided
	 */
	private void parseBody(String body) throws ArrayIndexOutOfBoundsException {

		this.uploadedFileName = body.split("=")[0];
		this.body = body.split("base64,")[1];
		this.uploadedFileExtension = body.split(":")[1].split(";")[0].split("/")[1];
	}

	/**
	 * This method read the body of request. It use a for loop around content
	 * length. Content-Length value we get from header. We read a char at a time
	 * because content length value belong to the amount of char present in the
	 * body. In addition, we have to check if char is '%' because we are using
	 * file reader in front end which change the image data into base64. If char
	 * is '%' it means next two chars is a hexadecimal number which presents the
	 * char present at this position. So for getting the original char we have
	 * to convert it into integer and then char again. We must increase i by two
	 * because we are reading two char at a time,
	 * 
	 * @param reader
	 * @return String
	 * @throws IOException
	 *             If I/O occurs while reading a char form reader
	 */
	private String readBody(BufferedReader reader) throws IOException {

		StringBuilder body = new StringBuilder();

		for (int i = 0; i < contentLength; i++) {
			char ch = (char) reader.read();

			if (ch == '%') {
				ch = (char) Integer.parseInt("" + (char) reader.read() + "" + (char) reader.read(), 16);
				i += 2;
			}

			body.append(ch);
		}

		return body.toString();
	}

	private RequestParser(MethodType type, String path, String body, boolean connectionClosed, String uploadedFileName,
			String uploadedFileExtension) {

		this.type = type;
		this.path = path;
		this.body = body;
		this.connectionClosed = connectionClosed;
		this.uploadedFileName = uploadedFileName;
		this.uploadedFileExtension = uploadedFileExtension;
	}

	public MethodType getMethodType() {
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