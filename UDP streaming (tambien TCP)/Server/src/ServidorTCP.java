import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServidorTCP {
	private ServerSocket serverSocket;
	private Boolean running = true;
	// logger of times for the clients sorted by time stamp
	private ConcurrentLinkedDeque<Entry<Long, Integer>> loging = new ConcurrentLinkedDeque<>();
	private AdministracionUsuarios administracionUsuarios;
	private AdministacionStreaming administacionStreaming;

	public ServidorTCP(AdministracionUsuarios adUsuarios, AdministacionStreaming administacionStreaming, int puerto,
			int maxThreads, String pathTime, String pathProcess) throws IOException {
		administracionUsuarios = adUsuarios;
		this.administacionStreaming = administacionStreaming;

		System.out.println("El servidor esta corriendo el en puerto: " + puerto);
		serverSocket = new ServerSocket(puerto);

		new Thread(() -> {
			try {
				// this structure manages the threads execution
				ExecutorService exec = Executors.newFixedThreadPool(maxThreads);
				int clientId = 0;
				while (isRunning()) {
					try {
						Socket socket = serverSocket.accept();
						socket.setKeepAlive(true);
						log(clientId, System.currentTimeMillis());
						// add the thread to the Executor service
						exec.execute(clientProtocol(socket, clientId));
						clientId++;
					} catch (SocketException e) {
						// a Exception is generated when the server socket is closed.s
					}
				}

				exec.shutdown();
				// Wait until all threads are finish
				exec.awaitTermination(30L, TimeUnit.SECONDS);
				report(pathTime, pathProcess);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * @return is the server running?
	 */
	private synchronized boolean isRunning() {
		return running;
	}

	/*
	 * break the cycle in the server and close the server socket
	 */
	public synchronized void finish() throws IOException {
		running = false;
		serverSocket.close();
	}

	/**
	 * 
	 * @param socket to communicate with the client
	 * @param id     of the client
	 * @return a runnable with the process of authentication.
	 */
	private Runnable clientProtocol(Socket socket, int id) {
		return () -> {
			log(id, System.currentTimeMillis());
			try (PrintStream pr = new PrintStream(socket.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

				String[] ans = br.readLine().split("&");
				String method = ans[0];
				String name = ans[1];
				String password = ans[2];

				System.out.println("method: " + method + ", name: " + name + ", password: " + password);

				if (method.equals("LOGIN") || method.equals("REGISTER")) {
					boolean answer = false;
					if (method.equals("LOGIN"))
						answer = administracionUsuarios.login(name, password);
					else if (method.equals("REGISTER"))
						answer = administracionUsuarios.register(name, password);

					if (answer)
						pr.println("OK");
					else
						pr.println("NO");
				} else if (method.equals("GET")) {
					if (password.equals("UDP")) {
						pr.println(administacionStreaming.getRoute(name));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			log(id, System.currentTimeMillis());
		};
	}

	/**
	 * this method add in a structure the important events for every user: when the
	 * client starts the TCP protocol when the communication is starting when the
	 * communication is over
	 * 
	 * @param id        the id of the client
	 * @param timestamp system time stamp. is necessarily a parameter in order to
	 *                  don't be affected by synchronized's queue
	 */
	private void log(int id, Long timestamp) {
		loging.push(new AbstractMap.SimpleEntry<>(timestamp, id));
	}

	/**
	 * uses the log to generate analysis
	 * 
	 * @param Max
	 * @param fileExport
	 * @throws IOException problems in writing
	 */
	private void report(String pathTime, String pathProcess) throws IOException {
		if (loging.isEmpty())
			return;
		StringBuilder strBuilder = new StringBuilder("timestap;#inquee;#inprocess\n");
		HashMap<Integer, Long> timeQueue = new HashMap<>();
		HashMap<Integer, Long> timeProcess = new HashMap<>();
		int inQuue = 0, inProcess = 0;

		PriorityQueue<Entry<Long, Integer>> queue = new PriorityQueue<>((a, b) -> (int) (a.getKey() - b.getKey()));
		queue.addAll(loging);

		long init = queue.peek().getKey();
		while (!queue.isEmpty()) {
			Entry<Long, Integer> map = queue.poll();
			Long ini = timeQueue.putIfAbsent(map.getValue(), map.getKey());
			if (ini != null) {
				Long ini2 = timeProcess.putIfAbsent(map.getValue(), map.getKey());
				if (ini2 == null) {
					// second log: start to process
					timeQueue.put(map.getValue(), map.getKey() - ini);
					inQuue--;
					inProcess++;
				} else {
					// Third log: finish
					timeProcess.put(map.getValue(), map.getKey() - ini2);
					inProcess--;
				}
			} else {
				// first log: enter to the queue
				inQuue++;
			}
			strBuilder.append(map.getKey() - init + ";" + inQuue + ";" + inProcess + "\n");
		}
		writeInFile(strBuilder, pathProcess);

		System.out.println("tiempo promedio en cola: " + mean(timeQueue.values()) + " ms");
		System.out.println("tiempo promedio en procesamiento: " + mean(timeProcess.values()) + "ms");
		strBuilder = new StringBuilder("clientId;timeQueue;timeProcess\n");
		for (Entry<Integer, Long> entry : timeQueue.entrySet()) {
			int id = entry.getKey();
			strBuilder.append(id + ";" + entry.getValue() + ";" + timeProcess.get(id) + "\n");
		}
		writeInFile(strBuilder, pathTime);
	}

	/**
	 * @param text     to write
	 * @param filePath of the file
	 * @throws IOException problems
	 */
	private void writeInFile(CharSequence text, String filePath) throws IOException {
		try (PrintStream pr = new PrintStream(filePath)) {
			pr.println(text);
		}
	}

	/**
	 * 
	 * @param collection
	 * @return the mean value of the collection
	 */
	private double mean(Collection<Long> collection) {
		if (collection.isEmpty())
			return 0;
		return collection.stream().reduce((a, b) -> a + b).get() / (double) collection.size();
	}
}
