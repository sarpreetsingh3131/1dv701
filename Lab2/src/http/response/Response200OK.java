package http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import http.ServerThread;

public class Response200OK extends Response {

	private final File file;

	public Response200OK(ServerThread client, File file) {
		super(client, "200 OK", "");
		this.file = file;
	}

	/**
	 * This class override this method because we have to write files to client
	 * whereas for all the other responses we are using Strings as a content.
	 * This method first split the file name into '.' for getting the extension.
	 * Eg: index.html will [index, html] and we get the last one as an
	 * extension. Then we simply write it.
	 */
	@Override
	public void write() throws IOException {

		String[] parts = file.getName().split("\\.");
		super.writeHeader(file.length(), parts[parts.length - 1]);
		writeFile();
	}

	/**
	 * This method write the file to client using FileInputStream and
	 * OutputStream from client socket. We read the bytes until its -1(means no
	 * more to read) and write to client. At last we close the OutputStream and
	 * print the detail of client including the requested filename.
	 * 
	 * @throws IOException
	 */
	private void writeFile() throws IOException {

		FileInputStream in = new FileInputStream(file);
		OutputStream out = super.client.getSocket().getOutputStream();

		int bytesRead = 0;
		while ((bytesRead = in.read(super.client.getBuffer())) != -1) {
			out.write(super.client.getBuffer(), 0, bytesRead);
		}
		in.close();

		System.out.println("Client " + super.client.getClientId() + " got " + file.getName());
	}
}