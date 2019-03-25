package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import logic.TCPautentificacion;

public class Frame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel l2, l3;
	private JTextField tf1;
	private JButton btn1, btn2;
	private JPasswordField p1;
	private TCPautentificacion tcpautentificacion;

	public Frame() {
		super("AUTENTIFICACION");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tcpautentificacion = new TCPautentificacion();
		l2 = new JLabel("Username");
		l3 = new JLabel("Password");
		tf1 = new JTextField();
		p1 = new JPasswordField();
		btn1 = new JButton("Register");
		btn1.addActionListener(this);
		btn2 = new JButton("login");
		btn2.addActionListener(this);

		l2.setBounds(80, 70, 200, 30);
		l3.setBounds(80, 110, 200, 30);
		tf1.setBounds(300, 70, 200, 30);
		p1.setBounds(300, 110, 200, 30);
		btn1.setBounds(150, 160, 100, 30);
		btn2.setBounds(300, 160, 100, 30);

		this.add(l2);
		this.add(tf1);
		this.add(l3);
		this.add(p1);
		this.add(btn1);
		this.add(btn2);

		this.setSize(600, 320);
		this.setLayout(null);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		String uname = tf1.getText();
		@SuppressWarnings("deprecation")
		String pass = p1.getText();
		boolean b = false;
		if (ae.getSource() == btn2) {
			// login
			b = tcpautentificacion.login(uname, pass);
		} else if (ae.getSource() == btn1) {
			// register
			b = tcpautentificacion.register(uname, pass);
		}
		if (b) {
			try {
				new Reproductor(tcpautentificacion);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Incorrect login or password / can't register", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new Frame();
	}
}