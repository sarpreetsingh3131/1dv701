package tftp.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public abstract class Packet {

	protected ByteBuffer byteBuffer;
	protected final static int MAX_SIZE = 516;
	private DatagramPacket packet;

	/**
	 * It creates a default empty packet
	 * 
	 * @param size
	 */
	public Packet(int size) {
		byteBuffer = ByteBuffer.allocate(size);
		packet = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length);
	}

	/**
	 * It create a packet according to the size and include opcode and block. In
	 * this we also specify the length of packet because for data packet we
	 * always need it as it depends on the bytes read
	 * 
	 * @param size
	 * @param opcode
	 * @param block
	 * @param length
	 */
	public Packet(int size, int opcode, int block, int length) {
		byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.putShort((short) opcode);
		byteBuffer.putShort((short) block);
		packet = new DatagramPacket(byteBuffer.array(), length);
	}

	/* Some handy methods */

	public short getOpcode() {
		return byteBuffer.getShort();
	}

	public short getBlock() {
		return byteBuffer.getShort();
	}

	public int getPort() {
		return packet.getPort();
	}

	public int getLength() {
		return packet.getLength();
	}

	public byte[] getData() {
		return packet.getData();
	}

	public DatagramPacket toDatagramPacket() {
		return packet;
	}
}