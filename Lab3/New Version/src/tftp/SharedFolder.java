package tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import tftp.exceptions.FileAlreadyExistsException;
import tftp.exceptions.OutOfMemoryException;

public class SharedFolder {

	private final File READ_DIR = new File("src/tftp/resources/read/");
	private final File WRITE_DIR = new File("src/tftp/resources/write/");
	private File file;
	private FileInputStream input;
	private FileOutputStream output;

	public int read(byte[] buffer) throws IOException {
		return input.read(buffer);
	}

	public int write(DatagramPacket packet) throws IOException, OutOfMemoryException {
		byte[] data = Arrays.copyOfRange(packet.getData(), 4, packet.getLength());

		if (WRITE_DIR.getFreeSpace() < (file.length() + data.length)) {
			file.delete();
			throw new OutOfMemoryException();
		}

		output.write(data);
		output.flush();
		return data.length;
	}

	public void setReadPath(String path) throws FileNotFoundException {
		file = new File(READ_DIR, path);

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		input = new FileInputStream(file);
	}

	public void setWritePath(String path) throws FileAlreadyExistsException, FileNotFoundException {
		file = new File(WRITE_DIR, path);

		if (file.exists()) {
			throw new FileAlreadyExistsException();
		}

		output = new FileOutputStream(file);
	}
}