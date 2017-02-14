package http.response;

import java.io.File;

public class Response200OK extends Response {

	private final String RESPONSE = "HTTP/1.1 200 OK\r\n";
	private final File file;
	private final ResponseType RESPONSE_TYPE = ResponseType.OK;

	public Response200OK(File file) {
		this.file = file;
	}

	@Override
	public String getResponseString(boolean connection) {
		String[] parts = file.getName().split("\\.");
		return RESPONSE + super.getContentLengthAndType(file.length(), parts[parts.length - 1]) + super.getConnection(connection);
	}

	@Override
	public ResponseType getResponseType() {
		return RESPONSE_TYPE;
	}

	@Override
	public String getContent() {
		return null;
	}

	@Override
	public File getFile() {
		return file;
	}
}