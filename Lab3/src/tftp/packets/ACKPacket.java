package tftp.packets;

public class ACKPacket extends Packet {

	public static final int OP_ACK = 4;

	/**
	 * It creates an empty ACK packet
	 */
	public ACKPacket() {
		super(OP_ACK);
	}

	/**
	 * It create an ACK packet which include its size, ACK code, block and
	 * packet length
	 * 
	 * @param block
	 */
	public ACKPacket(int block) {
		super(OP_ACK, OP_ACK, block, OP_ACK);
	}
}