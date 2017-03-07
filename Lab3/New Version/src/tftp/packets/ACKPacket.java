package tftp.packets;

public class ACKPacket extends Packet {

	public static final int OP_ACK = 4;

	public ACKPacket() {
		super(OP_ACK);
	}

	public ACKPacket(int block) {
		super(OP_ACK, OP_ACK, block);
	}
}