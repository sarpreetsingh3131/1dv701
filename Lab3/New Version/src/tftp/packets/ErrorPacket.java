package tftp.packets;

import tftp.errors.Error;

public class ErrorPacket extends Packet {

	public final static int OP_ERR = 5;

	public ErrorPacket(Error error) {
		super(MAX_SIZE, OP_ERR, error.getCode(), MAX_SIZE);
		byteBuffer.put(error.getMsg().getBytes());
		byteBuffer.put((byte) 0);
	}
}