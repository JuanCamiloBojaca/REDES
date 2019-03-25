package vista;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import logic.TCPautentificacion;
import logic.UDPStreamer;

public class Reproductor extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel l1;
	private JTextField tf1;
	private JButton btn1;
	private JLabel iconLabel = new JLabel();
	private ImageIcon icon;
	private JPanel mainPanel = new JPanel();

	private Timer timer; // timer used to receive data from the UDP socket
	private UDPStreamer udpStreamer = new UDPStreamer();
	private TCPautentificacion tcpautentificacion;

	public Reproductor(TCPautentificacion tcpautentificacion) throws IOException {
		super("REPRODUCION");
		this.tcpautentificacion = tcpautentificacion;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		l1 = new JLabel("canal");
		tf1 = new JTextField();
		btn1 = new JButton("get");
		icon = new ImageIcon("./data/hola.jpg");
		iconLabel = new JLabel(icon);

		l1.setBounds(5, 5, 100, 30);
		tf1.setBounds(110, 5, 200, 30);
		btn1.setBounds(320, 5, 100, 30);
		iconLabel.setBounds(5, 40, 600, 400);
		btn1.addActionListener(this);

		mainPanel.setLayout(null);
		mainPanel.add(l1);
		mainPanel.add(tf1);
		mainPanel.add(btn1);
		mainPanel.add(iconLabel);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
		setSize(690, 560);
		this.setVisible(true);

		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				icon = new ImageIcon(udpStreamer.receive());
				iconLabel.setIcon(icon);
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent ae) {
		String canal = tf1.getText();
		timer.stop();
		String ans = tcpautentificacion.get(canal);
		if (ans.equals("NO"))
			JOptionPane.showMessageDialog(this, "chanel not found", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			String[] asn = ans.split("-");
			try {
				udpStreamer.connect(asn[0], Integer.parseInt(asn[1]));
				timer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}