package tftp.packets;

import tftp.errors.Error;

public class ErrorPacket extends Packet {

	public final static int OP_ERR = 5;

	public ErrorPacket(Error error) {
		super(error.getMsg().length() + OP_ERR, OP_ERR, error.getCode());
		super.setData(error.getMsg().getBytes());
	}
}