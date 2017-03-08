package tftp.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public abstract class Packet {

	protected ByteBuffer byteBuffer;
	protected final static int MAX_SIZE = 516;
	private DatagramPacket packet;

	public Packet(int size) {
		byteBuffer = ByteBuffer.allocate(size);
		packet = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length);
	}

	public Packet(int size, int opcode, int block, int length) {
		byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.putShort((short) opcode);
		byteBuffer.putShort((short) block);
		packet = new DatagramPacket(byteBuffer.array(), length);
	}
	
	public short getOpcode() {
		return byteBuffer.getShort();
	}

	public short getBlock() {
		return byteBuffer.getShort();
	}

	public DatagramPacket toDatagramPacket() {
		return packet;
	}
}