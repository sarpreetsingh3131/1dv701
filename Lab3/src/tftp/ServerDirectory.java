package tftp;

import java.io.*;
import tftp.exceptions.*;

public class ServerDirectory {

	private final File READ_DIR = new File("src/tftp/resources/read/");
	private final File WRITE_DIR = new File("src/tftp/resources/write/");
	private File file;
	private FileInputStream input;
	private FileOutputStream output;

	/**
	 * This method read the requested file and save it in the buffer and returns
	 * number of bytes read
	 * 
	 * @param buffer
	 * @return
	 * @throws NoSuchUserException
	 *             If error occurs while reading the file
	 */
	public int read(byte[] buffer) throws NoSuchUserException {
		try {
			return input.read(buffer);
		} catch (IOException e) {
			throw new NoSuchUserException();
		}
	}

	/**
	 * This method write a file in the server directory and return the length of
	 * written data.
	 * 
	 * @param data
	 * @return
	 * @throws OutOfMemoryException
	 *             When disk space is low
	 * @throws AccessViolationException
	 *             When I/O occurs
	 */
	public int write(byte[] data) throws OutOfMemoryException, AccessViolationException {
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

	/**
	 * It create a new file which client will read
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 *             When requested file is not found
	 */
	public void setReadPath(String path) throws FileNotFoundException {
		file = new File(READ_DIR, path);
		input = new FileInputStream(file);
	}

	/**
	 * It create a new file which client will write.
	 * 
	 * @param path
	 * @throws FileAlreadyExistsException
	 *             If file already existsF
	 * @throws AccessViolationException
	 *             When I/O occurs
	 */
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

	/**
	 * Delete the file
	 */
	public void deleteFile() {
		file.delete();
	}

	/**
	 * It close {@link FileInputStream}.
	 * 
	 * @throws NoSuchUserException
	 *             When I/O occurs while closing
	 */
	public void closeInputStream() throws NoSuchUserException {
		try {
			input.close();
		} catch (IOException e) {
			throw new NoSuchUserException();
		}
	}

	/**
	 * It close the {@link FileOutputStream}
	 * 
	 * @throws AccessViolationException
	 *             When I/O occurs while closing
	 */
	public void closeOutputStream() throws AccessViolationException {
		try {
			output.close();
		} catch (IOException e) {
			throw new AccessViolationException();
		}
	}
}