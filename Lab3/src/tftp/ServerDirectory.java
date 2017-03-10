package tftp;

import java.io.*;
import tftp.exceptions.*;

public class ServerDirectory {

	private final File READ_DIR = new File("src/tftp/resources/read/");
	private final File WRITE_DIR = new File("src/tftp/resources/write/");
	private final String ALLOWED_IP = "192.0.0.1";
	private File file;
	private FileInputStream input;
	private FileOutputStream output;

	/**
	 * This method read the requested file and save it in the buffer and returns
	 * number of bytes read
	 * 
	 * @param buffer
	 * @return
	 * @throws AccessViolationException
	 */
	public int read(byte[] buffer) throws AccessViolationException {
		try {
			return input.read(buffer);
		} catch (IOException e) {
			throw new AccessViolationException();
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
	 *             When file is not locked or not readable
	 */
	public int write(byte[] data) throws OutOfMemoryException, AccessViolationException {

		if (WRITE_DIR.getFreeSpace() < (file.length() + data.length)) {
			file.delete();
			throw new OutOfMemoryException();
		}

		try {
			output.write(data);
			output.flush();
		} catch (IOException e) {
			throw new AccessViolationException();
		}

		return data.length;
	}

	/**
	 * It create a new file which client will read. In addition, we also check
	 * if requested file is in the server directory because a client can use
	 * './' or '../' to change the folder. Canonical path helps to verify this
	 * and throw error if file denies the access. For no such user we made a
	 * folder called 'personal' and only client with 'ALLOWED_IP' address can
	 * access this, otherwise we return error
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 *             When requested file is not found
	 * @throws AccessViolationException
	 *             When file is not locked or not readable
	 * @throws NoSuchUserException
	 */
	public void setReadPath(String path, String host)
			throws FileNotFoundException, AccessViolationException, NoSuchUserException {
		file = new File(READ_DIR, path);
		System.out.println(host);
		try {
			if (!file.getCanonicalPath().substring(0, READ_DIR.getAbsolutePath().length())
					.equals(READ_DIR.getAbsolutePath())) {
				throw new AccessViolationException();
			}

			else if (file.getCanonicalPath().startsWith((READ_DIR.getAbsolutePath()) + "/personal")
					&& !host.equals(ALLOWED_IP)) {
				throw new NoSuchUserException();
			}

			input = new FileInputStream(file);
		} catch (IOException e) {
			throw new AccessViolationException();
		}
	}

	/**
	 * It create a new file which client will write.
	 * 
	 * @param path
	 * @throws FileAlreadyExistsException
	 *             If file already existsF
	 * @throws AccessViolationException
	 *             When directory is read only
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

	/* Some handy methods */
	public void deleteFile() {
		file.delete();
	}

	public void closeInputStream() throws AccessViolationException {
		try {
			input.close();
		} catch (IOException e) {
			throw new AccessViolationException();
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