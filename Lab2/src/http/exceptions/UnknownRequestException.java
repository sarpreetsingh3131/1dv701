package http.exceptions;

public class UnknownRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownRequestException(String request) {
		super(request);
	}
}