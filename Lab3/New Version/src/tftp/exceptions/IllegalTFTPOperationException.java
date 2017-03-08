package tftp.exceptions;

public class IllegalTFTPOperationException extends Exception {

	private static final long serialVersionUID = 1L;
	private final String msg;

	public IllegalTFTPOperationException(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
