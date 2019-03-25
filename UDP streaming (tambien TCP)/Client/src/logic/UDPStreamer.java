package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPStreamer {
	byte[] buf; // buffer used to store data received from the server
	private MulticastSocket RTSPsocket;
	
	public UDPStreamer() {
		buf =  new byte[50000];
	}
	
	public byte[] receive() throws IOException {
		DatagramPacket rcvdp = new DatagramPacket(buf, buf.length);
		RTSPsocket.receive(rcvdp);
		RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());
		int payload_length = rtp_packet.getpayload_length();
		byte[] payload = new byte[payload_length];
		rtp_packet.getpayload(payload);
		
		return payload;
	}
	
	public void connect(String direcion,int port) throws IOException {
		RTSPsocket = new MulticastSocket(port);
		InetAddress group = InetAddress.getByName(direcion);
		RTSPsocket.joinGroup(group);
	}

}
