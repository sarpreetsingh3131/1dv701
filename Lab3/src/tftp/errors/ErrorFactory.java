package tftp.errors;

import java.io.IOException;
import java.net.DatagramSocket;
import tftp.packets.ErrorPacket;

public class ErrorFactory {

	private DatagramSocket sendSocket;

	public ErrorFactory(DatagramSocket sendSocket) {
		this.sendSocket = sendSocket;
	}

	/* Some handy methods */

	public void sendError0NotDefined(String msg) {
		send(new Error0NotDefined(msg));
	}

	public void sendError1FileNotFound() {
		send(new Error1FileNotFound());
	}

	public void sendError2AccessViolation() {
		send(new Error2AccessViolation());
	}

	public void sendError3DiskFullOrAllocationExceeded() {
		send(new Error3DiskFullOrAllocationExceeded());
	}

	public void sendError4IllegalTFTPOperation() {
		send(new Error4IllegalTFTPOperation());
	}

	public void sendError5UnknownTransferID() {
		send(new Error5UnknownTransferID());
	}

	public void sendError6FileAlreadyExists() {
		send(new Error6FileAlreadyExits());
	}

	public void sendError7NoSuchUser() {
		send(new Error7NoSuchUser());
	}

	private void send(Error error) {
		try {
			sendSocket.send(new ErrorPacket(error).toDatagramPacket());
		} catch (IOException e) {
		}
	}
}