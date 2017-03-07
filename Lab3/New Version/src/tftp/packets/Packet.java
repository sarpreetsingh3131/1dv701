package tftp.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public abstract class Packet {

	protected ByteBuffer byteBuffer;

	public Packet(int size) {
		byteBuffer = ByteBuffer.allocate(size);
	}

	public Packet(int size, int opcode, int block) {
		byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.putShort((short) opcode);
		byteBuffer.putShort((short) block);
	}

	protected void setData(byte[] data) {
		byteBuffer.put(data);
	}
	
	public short getOpcode() {
		return byteBuffer.getShort();
	}
	
	public short getBlock() {
		return byteBuffer.getShort();
	}

	public DatagramPacket toDatagramPacket() {
		return new DatagramPacket(byteBuffer.array(), byteBuffer.array().length);
	}
}