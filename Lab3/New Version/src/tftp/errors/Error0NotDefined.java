package tftp.errors;

public class Error0NotDefined extends Error {
	
	public Error0NotDefined() {
		super("Not defined", 0);
	}
	
	public Error0NotDefined(String msg) {
		super(msg, 0);
	}
}