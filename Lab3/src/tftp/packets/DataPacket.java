package tftp.packets;

public class DataPacket extends Packet {

	public final static int OP_DAT = 3;

	/**
	 * It create a data packet with size, OP_DAT, block and packet length. We
	 * add 4 in the length because Packet size is 516 and out of that 512 is
	 * data and 4 is for header such as 2 for OP_DATA and 2 for block. At last,
	 * we also add data into the packet
	 * 
	 * @param block
	 * @param data
	 * @param bytesRead
	 */
	public DataPacket(int block, byte[] data, int bytesRead) {
		super(MAX_SIZE, OP_DAT, block, bytesRead + 4);
		byteBuffer.put(data);
	}

	/**
	 * It create an empty data packet
	 */
	public DataPacket() {
		super(MAX_SIZE);
	}
}