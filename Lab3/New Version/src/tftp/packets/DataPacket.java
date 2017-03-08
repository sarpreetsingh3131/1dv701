package tftp.packets;

public class DataPacket extends Packet {

	public final static int OP_DAT = 3;

	public DataPacket(int block, byte[] data, int bytesRead) {
		super(MAX_SIZE, OP_DAT, block, bytesRead + 4);
		byteBuffer.put(data);
	}

	public DataPacket() {
		super(MAX_SIZE);
	}
}