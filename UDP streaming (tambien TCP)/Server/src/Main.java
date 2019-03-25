import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Main extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel l1, l2, l3;
	private JTextField tf1, tf2, tf3;
	private JButton btn1;
	private AdministacionStreaming administacionStreaming;

	public Main(AdministacionStreaming administacionStreaming) {
		this.administacionStreaming = administacionStreaming;
		l1 = new JLabel("nombre");
		tf1 = new JTextField();
		l2 = new JLabel("puerto");
		tf2 = new JTextField();
		l3 = new JLabel("direcion");
		tf3 = new JTextField();
		btn1 = new JButton("selecionar archivo");

		add(l1);
		add(tf1);
		add(l2);
		add(tf2);
		add(l3);
		add(tf3);
		add(btn1);

		l1.setBounds(5, 5, 50, 30);
		tf1.setBounds(55, 5, 200, 30);
		l2.setBounds(5, 40, 50, 30);
		tf2.setBounds(55, 40, 200, 30);
		l3.setBounds(5, 75, 50, 30);
		tf3.setBounds(55, 75, 200, 30);
		btn1.setBounds(4, 120, 200, 30);

		btn1.addActionListener(this);

		setSize(300, 200);
		this.setLayout(null);
		this.setVisible(true);
	}

	/**
	 * @return thread to stop the server when the user write something
	 */
	private static Thread stopThread(Main main, ServidorTCP servidorTCP,
			AdministacionStreaming administacionStreaming) {
		return new Thread(() -> {
			System.out.println("dijite cualquier cosa para finalizar");
			try (Scanner sc = new Scanner(System.in)) {
				sc.nextLine(); // in this line scanner is waiting
				System.out.println("cerrando...");
				servidorTCP.finish();
				administacionStreaming.finishAll();
				main.dispose();
			} catch (IOException e) {
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = tf1.getText();
		String dir = tf3.getText();
		int port = Integer.valueOf(tf2.getText());

		FileDialog dialog = new FileDialog(this, "Select File to Open");
		dialog.setMode(FileDialog.LOAD);
		dialog.setVisible(true);
		String file = dialog.getDirectory() + dialog.getFile();
		System.out.println(file + " chosen." + name + "  " + dir + "  " + port);
		try {
			administacionStreaming.addCanal(name, port, dir, file);
		} catch (Exception es) {
			es.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Properties pr = new Properties();
		try (FileInputStream fl = new FileInputStream("data/properties")) {
			pr.load(fl);
			AdministracionUsuarios administracionUsuarios = new AdministracionUsuarios();
			int port = Integer.parseInt(pr.getProperty("port"));
			int maxThreads = Integer.parseInt(pr.getProperty("maxThreads"));

			AdministacionStreaming administacionStreaming = new AdministacionStreaming();
			administacionStreaming.load();

			ServidorTCP servidorTCP = new ServidorTCP(administracionUsuarios, administacionStreaming, port, maxThreads,
					pr.getProperty("pathTimes"), pr.getProperty("pathProcess"));

			Main m = new Main(administacionStreaming);
			stopThread(m, servidorTCP, administacionStreaming).start();
		}
	}

}
