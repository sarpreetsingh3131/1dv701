package http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import http.ClientThread;
import http.exceptions.InternalServerException;

/*
 * This class forms the specific 200OK response.
 */
public class Response200OK extends Response {

	private final File file;
	
	// Creates response with file.
	public Response200OK(ClientThread client, File file) {
		super(client, "200 OK");
		this.file = file;
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
			OutputStream out = super.client.getSocket().getOutputStream();
			
			// Sends the file through the stream.
			int bytesRead = 0;
			while ((bytesRead = in.read(super.client.getBuffer())) != -1) {
				out.write(super.client.getBuffer(), 0, bytesRead);
			}
			in.close();
		} catch (Exception e) {
			throw new InternalServerException();
		}
		System.out.println("Client " + super.client.getClientId() + " got " + file.getName());
	}
}