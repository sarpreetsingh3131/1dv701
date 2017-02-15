public class Main {

	public static void main(String[] args) {
			//NetworkLayer layer = new UDPEchoClient(args);
			NetworkLayer layer = new TCPEchoClient(args);
			layer.runClient();
	}
}