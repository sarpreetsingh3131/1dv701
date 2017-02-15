import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class NetworkLayer implements Runnable {

	protected int bufSize = 1024;
	protected int transferRate;
	protected String IP;
	protected int port = 4950;
	protected byte[] buf;
	protected SocketAddress localBindPoint;
	protected SocketAddress remoteBindPoint;
	private final int MYPORT = 0;
	private long time = 0;

	public NetworkLayer(String[] args) {
		isValidData(args);

		// create local and remote point
		localBindPoint = new InetSocketAddress(MYPORT);
		remoteBindPoint = new InetSocketAddress(IP, port);
	}

	// This method will be implemented in Client class according to the needs.
	public abstract void runClient();

	protected void delay() {
		try {
			Thread.sleep(1000 / transferRate);
		} catch (InterruptedException e) {
			return;
			// catch exception if time is out and thread is in the delay
		}
	}

	// compare sent and receive message
	protected void compareSentAndReceivedMessage(String sentMessage, String receivedMessage) {
		if (receivedMessage.compareTo(sentMessage) == 0)
			System.out.printf("%d bytes sent and received [Buffer Size = " + bufSize + "]\n", receivedMessage.length());
		else
			System.out.print("[Sent " + sentMessage.length() + " bytes] [Received " + receivedMessage.length()
					+ " bytes] msg not equal! [Buffer Size = " + bufSize + "]\n");
	}

	protected void runForOneSecond(Thread thread) {
		// run process for 995 ms, to get approximately 1 second
		final int RUNTIME = 995;

		// create thread pool
		ExecutorService exec = Executors.newFixedThreadPool(1);

		// submit the thread class in it
		exec.submit(thread);

		// stop submitting new threads
		exec.shutdown();

		// run the process
		try {
			time = System.currentTimeMillis();
			exec.awaitTermination(RUNTIME, TimeUnit.MILLISECONDS);
			time = System.currentTimeMillis() - time;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// stop the thread
		exec.shutdownNow();
	}

	protected void displayTime(int sentMessages) {
		System.out.println("Time: " + time + " ms      Message left: " + (transferRate - sentMessages));
	}

	private void isValidData(String[] args) {
		if (args.length != 4) {
			System.out.println("Error: Incorrect number of arguments!! Please provide [IP] [PORT] [TRT] [BUF]");
			System.exit(1);
		}

		IP = args[0];
		port = convertToDigit(args[1]);
		bufSize = convertToDigit(args[2]);
		transferRate = convertToDigit(args[3]);

		// Check IP, it must contain four dots
		if (IP.isEmpty() || IP.split("\\.").length != 4) {
			System.out.println("Error: IP address is not valid!!");
			System.exit(1);
		}

		// split IP by '.' into 4 parts
		for (int i = 0; i < IP.split("\\.").length; i++) {
			try {
				if (Integer.parseInt(IP.split("\\.")[i]) > 255 || Integer.parseInt(IP.split("\\.")[i]) < 0) { // Range
					System.out.println("Error: IP address is not valid!!");
					System.exit(1);
				}
			} catch (Exception e) {
				// If contains other than integers
				System.out.println("Error: IP address is not valid!!");
				System.exit(1);
			}
		}

		// Check port
		if (port > 65535 || port < 1) {
			System.out.println("Error: Incorrect port number. Must be between (1-65535)");
			System.exit(1);
		}

		// Check buffer size
		if (bufSize < 1) {
			System.out.println("Error: Incorrect buffer size!!");
			System.exit(1);
		}

		// check out of memory exception
		try {
			buf = new byte[bufSize];
		} catch (OutOfMemoryError e) {
			System.out.println("Error: Buffer size is too big!!");
			System.exit(1);
		}

		// check if transfer rate time is less than 0
		if (transferRate < 0) {
			System.out.println("Error: Incorrect transfer rate time!!");
			System.exit(1);
		}

		// for sending at least 1 time
		transferRate = (transferRate == 0) ? 1 : transferRate;
	}

	// convert given arguments(IP, port, buffer size) into integer
	private int convertToDigit(String input) {
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			return -1; // If contains other than integers
		}
	}
}