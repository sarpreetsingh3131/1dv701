package tftp;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Arrays;
import tftp.exceptions.*;

public class ServerDirectory {

	private final File READ_DIR = new File("src/tftp/resources/read/");
	private final File WRITE_DIR = new File("src/tftp/resources/write/");
	private File file;
	private FileInputStream input;
	private FileOutputStream output;

	public int read(byte[] buffer) throws NoSuchUserException {
		try {
			return input.read(buffer);
		} catch (IOException e) {
			throw new NoSuchUserException();
		}
	}

	public int write(DatagramPacket packet) throws OutOfMemoryException, AccessViolationException {
		byte[] data = Arrays.copyOfRange(packet.getData(), 4, packet.getLength());

		if (WRITE_DIR.getFreeSpace() < (file.length() + data.length)) {
			file.delete();
			throw new OutOfMemoryException();
		}

		try {
			output.write(data);
			output.flush();
			return data.length;

		} catch (IOException e) {
			throw new AccessViolationException();
		}
	}

	public void setReadPath(String path) throws FileNotFoundException {
		file = new File(READ_DIR, path);
		input = new FileInputStream(file);
	}

	public void setWritePath(String path) throws FileAlreadyExistsException, AccessViolationException {
		file = new File(WRITE_DIR, path);

		if (file.exists()) {
			throw new FileAlreadyExistsException();
		}

		try {
			output = new FileOutputStream(file);
		} catch (IOException e) {
			throw new AccessViolationException();
		}
	}

	public void deleteFile() {
		file.delete();
	}

	public void closeInputStream() throws NoSuchUserException {
		try {
			input.close();
		} catch (IOException e) {
			throw new NoSuchUserException();
		}
	}

	public void closeOutputStream() throws AccessViolationException {
		try {
			output.close();
		} catch (IOException e) {
			throw new AccessViolationException();
		}
	}
}