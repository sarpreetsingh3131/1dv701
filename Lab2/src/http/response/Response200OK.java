package http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import http.exceptions.InternalServerException;

public class Response200OK extends Response {

	private final File file;
	private byte[] buffer;
	private final String RESPONSE = "HTTP/1.1 200 OK\r\n";

	public Response200OK(File file, Socket socket, byte[] buffer) {
		super(socket);
		this.file = file;
		this.buffer = buffer;
	}
	
	@Override
	public void write() throws InternalServerException {
		String[] parts = file.getName().split("\\.");
		super.writeHeader(RESPONSE, file.length(), parts[parts.length - 1]);
		writeFile();
	}

	private void writeFile() throws InternalServerException {
		try {
			FileInputStream in = new FileInputStream(file);
			OutputStream out = super.socket.getOutputStream();

			int bytesRead = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			in.close();
		} catch (Exception e) {
			throw new InternalServerException();
		}
	}
}