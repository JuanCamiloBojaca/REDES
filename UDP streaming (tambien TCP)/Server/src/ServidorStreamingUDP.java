import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class ServidorStreamingUDP extends MediaListenerAdapter {
	private static int MJPEG_TYPE = 26;
	private static int FRAME_PERIOD = 22; // Frame period of the video to stream, in ms

	private ScheduledExecutorService scheduler;
	private MulticastSocket UDPSocket;
	private int port;
	private String direcion;
	private InetAddress group;

	private IMediaReader reader;

	public ServidorStreamingUDP(int port, String direcionMulticast, String nombre) throws Exception {
		this.direcion = direcionMulticast;
		this.port = port;
		scheduler = new ScheduledThreadPoolExecutor(1);
		// videoStream = new VideoStream(port);

		UDPSocket = new MulticastSocket(port);
		group = InetAddress.getByName(direcionMulticast);
		UDPSocket.joinGroup(group);

		reader = ToolFactory.makeReader("./data/videos/" + port + "/file.mp4");
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader.addListener(this);

		scheduler.scheduleAtFixedRate(readPacket(), 1000, FRAME_PERIOD, TimeUnit.MILLISECONDS);
		System.out.println("nuevo canal: " + nombre + " corriendo en la direcion multicast: " + direcionMulticast
				+ " y en el puerto: " + port);
	}

	public Runnable readPacket() {
		return () -> {
			try {
				if (reader.readPacket() != null) {
					reader = ToolFactory.makeReader("./data/videos/" + port + "/file.mp4");
					reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
					reader.addListener(this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			BufferedImage a = event.getImage();
			//a = resize(a, 480, 360);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(a, "jpg", outputStream);
			outputStream.flush();
			byte[] buf = outputStream.toByteArray();

			RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, event.getStreamIndex(),
					event.getStreamIndex() * FRAME_PERIOD, buf, buf.length);

			int packet_length = rtp_packet.getlength();

			byte[] packet_bits = new byte[packet_length];
			rtp_packet.getpacket(packet_bits);

			// send the packet as a DatagramPacket over the UDP socket
			DatagramPacket datagramPacket = new DatagramPacket(packet_bits, packet_length, group, port);
			UDPSocket.send(datagramPacket);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getPort() {
		return port;
	}

	public String getRoute() {
		return direcion + "-" + port;
	}

	/*
	 * stop
	 */
	public synchronized void finish() throws IOException, InterruptedException {
		scheduler.shutdown();
		// Wait until all threads are finish
		scheduler.awaitTermination(10L, TimeUnit.SECONDS);
		UDPSocket.close();
	}
}
