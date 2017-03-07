package tftp;

public class Server {

	public static final int PORT = 4970;

	public static void main(String[] args) {
		if (args.length > 0) {
			System.err.printf("usage: java %s\n", ServerThread.class.getCanonicalName());
			System.exit(1);
		}

		ServerThread serverThread = new ServerThread(PORT);
		serverThread.start();
	}
}