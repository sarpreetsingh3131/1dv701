package http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import http.exceptions.InternalServerException;

/*
 * This class forms the specific 200OK response.
 */
public class Response200OK extends Response {

	private final File file;
	private byte[] buffer;
	
	// Creates response with file.
	public Response200OK(File file, Socket socket, byte[] buffer) {
		super(socket, "200 OK");
		this.file = file;
		this.buffer = buffer;
	}

	// Overrides the write method to also write a file.
	@Override
	public void write() throws InternalServerException {
		// Split to get the file extension.
		String[] parts = file.getName().split("\\.");
		
		super.writeHeader(file.length(), parts[parts.length - 1]);
		writeFile();
	}

	// Writes the file.
	private void writeFile() throws InternalServerException {
		try {
			// Input stream reading from file.
			FileInputStream in = new FileInputStream(file);
			// Output stream to the socket.
			OutputStream out = super.socket.getOutputStream();
			
			// Sends the file through the stream.
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