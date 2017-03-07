package tftp.packets;

import java.net.DatagramPacket;

public class DataPacket extends Packet {

	public final static int OP_DAT = 3;
	private final static int SIZE = 516;
	private final int BYTES_READ;

	public DataPacket(int block, byte[] data, int bytesRead) {
		super(SIZE, OP_DAT, block);
		super.setData(data);
		BYTES_READ = bytesRead + 4;
	}

	public DataPacket() {
		super(SIZE);
		BYTES_READ = SIZE;
	}

	@Override
	public DatagramPacket toDatagramPacket() {
		return new DatagramPacket(byteBuffer.array(), BYTES_READ);
	}
}