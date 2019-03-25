import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class AdministacionStreaming {
	private Map<String, ServidorStreamingUDP> canales = new HashMap<String, ServidorStreamingUDP>();

	public void addCanal(String nombre, int port, String direcionMulticast) throws Exception {
		ServidorStreamingUDP servidorStreamingUDP = new ServidorStreamingUDP(port, direcionMulticast, nombre);
		canales.put(nombre, servidorStreamingUDP);
	}

	public void addCanal(String nombre, int port, String direcionMulticast, String rutaMp4) throws Exception {
		(new File("./data/videos/" + port)).mkdir();

		System.out.println("comenzo la creacion del archivo");
		new mp4CanalCreator(rutaMp4, port);
		System.out.println("listo :)");
		
		ServidorStreamingUDP servidorStreamingUDP = new ServidorStreamingUDP(port, direcionMulticast, nombre);
		canales.put(nombre, servidorStreamingUDP);
		
		Properties pr = new Properties();
		try (FileInputStream fl = new FileInputStream("data/videos/canales")) {
			pr.load(fl);
			pr.put(nombre, port + ";" + direcionMulticast);
		}
		try (FileOutputStream fo = new FileOutputStream("data/videos/canales")) {
			pr.store(fo, "");
		}
	}

	public String getRoute(String canal) {
		if (!canales.containsKey(canal))
			return "NO";
		return canales.get(canal).getRoute();
	}

	public void load() throws Exception {
		Properties pr = new Properties();
		try (FileInputStream fl = new FileInputStream("data/videos/canales")) {
			pr.load(fl);
			for (Entry<Object, Object> entry : pr.entrySet()) {
				String[] a = ((String) entry.getValue()).split(";");
				addCanal((String) entry.getKey(), Integer.parseInt(a[0]), a[1]);
			}
		}
	}

	public void finishAll() {
		try {
			for (ServidorStreamingUDP serv : canales.values())
				serv.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
