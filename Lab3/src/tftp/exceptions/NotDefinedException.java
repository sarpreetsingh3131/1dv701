package tftp.exceptions;

public class NotDefinedException extends Exception {

	private static final long serialVersionUID = 1L;
	private final String msg;

	public NotDefinedException(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
