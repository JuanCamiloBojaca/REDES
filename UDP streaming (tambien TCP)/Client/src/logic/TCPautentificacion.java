package logic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;

public class TCPautentificacion {
	private int port;
	private String address;

	public TCPautentificacion() {
		Properties pr = new Properties();
		try (FileInputStream fl = new FileInputStream("data/properties")) {
			pr.load(fl);
			port = Integer.parseInt(pr.getProperty("port"));
			address = pr.getProperty("address");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean login(String name, String password) {
		try (Socket socket = new Socket(address, port)) {
			try (PrintStream pr = new PrintStream(socket.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				pr.println("LOGIN&" + name + "&" + password);
				String ans = br.readLine();
				return ans.equals("OK");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean register(String name, String password) {
		try (Socket socket = new Socket(address, port)) {
			try (PrintStream pr = new PrintStream(socket.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				pr.println("REGISTER&" + name + "&" + password);
				String ans = br.readLine();
				return ans.equals("OK");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String get(String name) {
		try (Socket socket = new Socket(address, port)) {
			try (PrintStream pr = new PrintStream(socket.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				pr.println("GET&" + name + "&UDP");
				String ans = br.readLine();
				return ans;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "NO";
	}
}
