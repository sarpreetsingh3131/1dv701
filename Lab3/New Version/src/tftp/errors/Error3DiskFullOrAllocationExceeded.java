package tftp.errors;

public class Error3DiskFullOrAllocationExceeded extends Error {

	public Error3DiskFullOrAllocationExceeded() {
		super("Disk full or allocation exceeded", 3);
	}
}